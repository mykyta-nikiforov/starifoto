package ua.in.photomap.geojsongenerator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.in.photomap.geojsongenerator.converter.PhotoFeatureConverter;
import ua.in.photomap.geojsongenerator.exception.GeojsonRegenerationException;
import ua.in.photomap.geojsongenerator.model.PhotoPageDTO;
import ua.in.photomap.geojsongenerator.service.PhotoFeatureService;
import ua.in.photomap.geojsongenerator.service.RegenerationService;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegenerationServiceImpl implements RegenerationService {
    private final WebClient webClient;
    private final PhotoFeatureService photoFeatureService;

    @Value("${photomap.photo-api.base-url}")
    private String PHOTO_API_BASE_URL;

    @Override
    public Mono<Boolean> regenerateCollection() {
        log.info("Starting collection regeneration...");
        final int pageSize = 500;

        // First, fetch the initial page to determine how many pages there are.
        return getPhotosPage(0, pageSize)
                .flatMapMany(firstPage -> {
                    if (firstPage.getLast() || firstPage.getContent().isEmpty()) {
                        // If the first page is the last one or empty, just process this single page.
                        return Flux.fromIterable(firstPage.getContent())
                                .map(PhotoFeatureConverter::convertToFeature)
                                .collectList()
                                .flatMap(features -> {
                                    log.info("Saving page of {} photo features", features.size());
                                    return photoFeatureService.saveAll(features)
                                            .doOnComplete(() -> log.info("Saved page 0 photo features."))
                                        .then();
                                })
                                .thenMany(Flux.empty()); // No more pages to process.
                    } else {
                        // If there are more pages, process the first page and then continue with the rest.
                        return Flux.concat(
                                // Process the first page immediately.
                                Flux.fromIterable(firstPage.getContent())
                                        .map(PhotoFeatureConverter::convertToFeature)
                                        .collectList()
                                        .flatMap(features -> {
                                            log.info("Saving page of {} photo features", features.size());
                                            return photoFeatureService.saveAll(features)
                                                    .doOnComplete(() -> log.info("Saved page 0 photo features."))
                                                    .then();
                                        }),
                                // Then process the remaining pages starting from page 1.
                                Flux.range(1, firstPage.getTotalPages() - 1) // This theoretically gives all subsequent pages.
                                        .concatMap(page -> getPhotosPage(page, pageSize)) // Maintain sequential processing
                                        .takeUntil(PhotoPageDTO::getLast) // Stop when the last page is reached.
                                        .concatMap(page -> Flux.fromIterable(page.getContent())
                                                .map(PhotoFeatureConverter::convertToFeature)
                                                .collectList()
                                                .flatMap(features -> {
                                                    log.info("Saving page {} of {} photo features", page.getNumber(), features.size());
                                                    return photoFeatureService.saveAll(features)
                                                            .collectList()
                                                            .doOnSuccess(list -> log.info("Saved page {} photo features. Size: {}", page.getNumber(), list.size()))
                                                            .doOnError(e -> log.error("Error while saving page {}", page.getNumber()))
                                                            .then();
                                                })
                                        )
                        );
                    }
                })
                .then(Mono.just(true)) // Return true when all pages have been processed.
                .doOnSuccess(result -> log.info("Collection regeneration completed successfully"))
                .onErrorResume(e -> {
                    log.error("Error regenerating collection", e);
                    return Mono.just(false); // Return false if an error occurs.
                })
                .onErrorMap(e -> new GeojsonRegenerationException("Failed to generate GeoJsons.", e));
    }

    private Mono<PhotoPageDTO> getPhotosPage(int page, int size) {
        log.info("Requesting photos: page={}, size={}", page, size);
        return webClient.get()
                .uri(PHOTO_API_BASE_URL + "/geojson/all" + "?page=" + page + "&size=" + size)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PhotoPageDTO>() {
                })
                .doOnSuccess(dto -> log.info("Received {} photos for page {}", dto.getContent().size(), page))
                .doOnError(e -> log.error("Failed to fetch photos for page {}", page, e));
    }
}

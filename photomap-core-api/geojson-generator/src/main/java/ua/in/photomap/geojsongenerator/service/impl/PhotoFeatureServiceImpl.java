package ua.in.photomap.geojsongenerator.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.in.photomap.geojsongenerator.converter.PhotoFeatureConverter;
import ua.in.photomap.geojsongenerator.model.PhotoFeature;
import ua.in.photomap.geojsongenerator.repository.PhotoFeatureRepository;
import ua.in.photomap.geojsongenerator.service.PhotoFeatureService;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoFeatureServiceImpl implements PhotoFeatureService {
    private final PhotoFeatureRepository photoFeatureRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> savePhotoFeature(String data) {
        return Mono.fromCallable(() -> objectMapper.readValue(data, PhotoGeoJsonDataDTO.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(photoDetailsDTO -> {
                    PhotoFeature photoFeature = PhotoFeatureConverter.convertToFeature(photoDetailsDTO);
                    return photoFeatureRepository.save(photoFeature)
                            .doOnNext(saved -> log.info("Saved photo: {}", saved))
                            .then();
                });
    }

    @Override
    public Mono<Void> deletePhotoFeature(Long photoId) {
        return photoFeatureRepository.deleteById(photoId)
                .doOnSuccess(aVoid -> log.info("Deleted photo with ID: {}", photoId));
    }

    @Override
    public Flux<PhotoFeature> saveAll(List<PhotoFeature> photoFeatures) {
        return photoFeatureRepository.saveAll(photoFeatures);
    }
}

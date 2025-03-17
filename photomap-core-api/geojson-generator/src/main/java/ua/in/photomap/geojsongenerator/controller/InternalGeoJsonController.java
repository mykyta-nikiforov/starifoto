package ua.in.photomap.geojsongenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.in.photomap.geojsongenerator.exception.GeojsonRegenerationException;
import ua.in.photomap.geojsongenerator.service.RegenerationService;

@RestController
@RequestMapping("/api/internal/geojson")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal GeoJSON", description = "Operations with GeoJSON data needed to store GeoJSON in MongoDB")
public class InternalGeoJsonController {
    private final RegenerationService regenerationService;

    @PostMapping("/regenerate")
    @Operation(summary = "Regenerate GeoJSON collection for all the photos. Used by photo-api.")
    public Mono<Boolean> regenerate() {
        return regenerationService.regenerateCollection()
                .onErrorMap(e -> {
                    log.info("Not regenerated because of error: ", e);
                    throw new GeojsonRegenerationException(e.getMessage());
                });
    }
}

package ua.in.photomap.geojsongenerator.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.in.photomap.geojsongenerator.model.PhotoFeature;

import java.util.List;

public interface PhotoFeatureService {
    Mono<Void> savePhotoFeature(String photoDto);

    Mono<Void> deletePhotoFeature(Long photoId);

    Flux<PhotoFeature> saveAll(List<PhotoFeature> photoFeatures);
}
package ua.in.photomap.geojsongenerator.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ua.in.photomap.geojsongenerator.model.PhotoFeature;

public interface PhotoFeatureRepository extends ReactiveMongoRepository<PhotoFeature, Long> {
}

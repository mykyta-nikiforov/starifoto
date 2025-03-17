package ua.in.photomap.geojsongenerator.service;

import reactor.core.publisher.Mono;

public interface RegenerationService {
    Mono<Boolean> regenerateCollection();
}

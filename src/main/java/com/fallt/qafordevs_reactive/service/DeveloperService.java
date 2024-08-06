package com.fallt.qafordevs_reactive.service;

import com.fallt.qafordevs_reactive.entity.DeveloperEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeveloperService {

    Mono<DeveloperEntity> createDeveloper(DeveloperEntity entity);

    Mono<DeveloperEntity> updateDeveloper(DeveloperEntity entity);

    Flux<DeveloperEntity> getAllDevelopers();

    Flux<DeveloperEntity> getAllActiveBySpecialty(String specialty);

    Mono<DeveloperEntity> getById(Integer id);

    Mono<Void> softDeleteById(Integer id);

    Mono<Void> hardDeleteById(Integer id);
}

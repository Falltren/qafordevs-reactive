package com.fallt.qafordevs_reactive.service;

import com.fallt.qafordevs_reactive.entity.DeveloperEntity;
import com.fallt.qafordevs_reactive.entity.Status;
import com.fallt.qafordevs_reactive.exception.DeveloperNotFoundException;
import com.fallt.qafordevs_reactive.exception.DeveloperWithEmailAlreadyExistsException;
import com.fallt.qafordevs_reactive.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {

    private final DeveloperRepository developerRepository;

    private Mono<Void> checkIfExistsByEmail(String email) {
        return developerRepository.findByEmail(email).flatMap(developer -> {
            if (Objects.nonNull(developer)) {
                return Mono.error(new DeveloperWithEmailAlreadyExistsException("Developer with defined email already exists", "DEVELOPER_DUPLICATE_EMAIL"));
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<DeveloperEntity> createDeveloper(DeveloperEntity developer) {
        return checkIfExistsByEmail(developer.getEmail())
                .then(Mono.defer(() -> {
                    developer.setStatus(Status.ACTIVE);
                    return developerRepository.save(developer);
                }));
    }

    @Override
    public Mono<DeveloperEntity> updateDeveloper(DeveloperEntity developer) {
        return developerRepository.findById(developer.getId())
                .switchIfEmpty(Mono.error(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND")))
                .flatMap(d -> developerRepository.save(developer));
    }

    @Override
    public Flux<DeveloperEntity> getAllDevelopers() {
        return developerRepository.findAll();
    }

    @Override
    public Flux<DeveloperEntity> getAllActiveBySpecialty(String specialty) {
        return developerRepository.findAllActiveBySpecialty(specialty);
    }

    @Override
    public Mono<DeveloperEntity> getById(Integer id) {
        return developerRepository.findById(id)
                .switchIfEmpty(Mono.error(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND")));
    }

    @Override
    public Mono<Void> softDeleteById(Integer id) {
        return developerRepository.findById(id)
                .switchIfEmpty(Mono.error(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND")))
                .flatMap(d -> {
                    d.setStatus(Status.DELETED);
                    return developerRepository.save(d).then();
                });
    }

    @Override
    public Mono<Void> hardDeleteById(Integer id) {
        return developerRepository.findById(id)
                .switchIfEmpty(Mono.error(new DeveloperNotFoundException("Developer not found", "DEVELOPER_NOT_FOUND")))
                .flatMap(d -> developerRepository.deleteById(id).then());
    }
}

package com.fallt.qafordevs_reactive.controller;

import com.fallt.qafordevs_reactive.dto.DeveloperDto;
import com.fallt.qafordevs_reactive.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/developers")
@RequiredArgsConstructor
public class DeveloperControllerV1 {

    private final DeveloperService developerService;

    @PostMapping
    public Mono<?> createDeveloper(@RequestBody DeveloperDto dto) {
        return developerService.createDeveloper(dto.toEntity())
                .flatMap(entity -> Mono.just(DeveloperDto.toDto(entity)));
    }

    @PutMapping
    public Mono<?> updateDeveloper(@RequestBody DeveloperDto dto) {
        return developerService.updateDeveloper(dto.toEntity())
                .flatMap(entity -> Mono.just(DeveloperDto.toDto(entity)));
    }

    @GetMapping
    public Flux<?> getAll() {
        return developerService.getAllDevelopers()
                .flatMap(entity -> Mono.just(DeveloperDto.toDto(entity)));
    }

    @GetMapping("/specialty/{specialty}")
    public Flux<?> getAllBySpecialty(@PathVariable("specialty") String specialty) {
        return developerService.getAllActiveBySpecialty(specialty)
                .flatMap(entity -> Mono.just(DeveloperDto.toDto(entity)));
    }

    @GetMapping("/{id}")
    public Mono<?> getById(@PathVariable("id") Integer id) {
        return developerService.getById(id)
                .flatMap(entity -> Mono.just(DeveloperDto.toDto(entity)));
    }

    @DeleteMapping("/{id}")
    public Mono<?> deleteById(@PathVariable("id") Integer id, @RequestParam(value = "isHard", defaultValue = "false") boolean isHard) {
        if (isHard) {
            return developerService.hardDeleteById(id);
        }
        return developerService.softDeleteById(id);
    }
}

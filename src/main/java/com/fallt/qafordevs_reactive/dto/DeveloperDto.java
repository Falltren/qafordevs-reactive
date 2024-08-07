package com.fallt.qafordevs_reactive.dto;

import com.fallt.qafordevs_reactive.entity.DeveloperEntity;
import com.fallt.qafordevs_reactive.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeveloperDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String specialty;

    private Status status;

    public static DeveloperDto toDto(DeveloperEntity entity) {
        return DeveloperDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .specialty(entity.getSpecialty())
                .status(entity.getStatus())
                .build();
    }

    public DeveloperEntity toEntity() {
        return DeveloperEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .specialty(specialty)
                .status(status)
                .build();
    }
}

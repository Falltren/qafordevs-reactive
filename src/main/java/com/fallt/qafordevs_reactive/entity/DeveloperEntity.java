package com.fallt.qafordevs_reactive.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("developers")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperEntity implements Persistable<Integer> {
    @Id
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String specialty;

    private Status status;

    @Override
    public boolean isNew() {
        return Objects.isNull(id);
    }
}

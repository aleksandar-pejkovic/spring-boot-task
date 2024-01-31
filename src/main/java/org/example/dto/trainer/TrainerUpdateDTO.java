package org.example.dto.trainer;

import org.example.enums.TrainingTypeName;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerUpdateDTO {

    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private TrainingTypeName specialization;

    private boolean isActive;
}

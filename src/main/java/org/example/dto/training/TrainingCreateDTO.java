package org.example.dto.training;

import java.util.Date;

import org.example.enums.TrainingTypeName;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingCreateDTO {

    @NotNull
    private String traineeUsername;

    @NotNull
    private String trainerUsername;

    @NotNull
    private TrainingTypeName trainingTypeName;

    @NotNull
    private Date trainingDate;

    private int trainingDuration;
}

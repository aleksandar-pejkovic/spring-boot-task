package org.example.dto.training;

import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingDTO {

    private long id;

    @NotNull
    private String trainingName;

    @NotNull
    private Date trainingDate;

    @NotNull
    private String trainingType;

    private int trainingDuration;

    @NotNull
    private String trainerName;
}

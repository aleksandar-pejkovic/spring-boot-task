package org.example.dto.training;

import java.util.Date;

import org.example.enums.TrainingTypeName;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingCreateDTO {

    private String traineeUsername;

    private String trainerUsername;

    private TrainingTypeName trainingTypeName;

    private Date trainingDate;

    private int trainingDuration;
}

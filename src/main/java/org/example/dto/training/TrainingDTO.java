package org.example.dto.training;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingDTO {

    private String trainingName;

    private Date trainingDate;

    private String trainingType;

    private int trainingDuration;

    private String trainerName;
}

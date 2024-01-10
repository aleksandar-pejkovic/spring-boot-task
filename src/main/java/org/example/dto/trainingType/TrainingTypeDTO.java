package org.example.dto.trainingType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingTypeDTO {

    private long id;

    private String trainingTypeName;
}

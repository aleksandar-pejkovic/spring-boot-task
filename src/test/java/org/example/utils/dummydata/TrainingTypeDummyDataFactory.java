package org.example.utils.dummydata;

import org.example.enums.TrainingTypeName;
import org.example.model.TrainingType;

public class TrainingTypeDummyDataFactory {

    private TrainingTypeDummyDataFactory() {
    }

    public static TrainingType getTrainingTypeAerobic() {
        return TrainingType.builder()
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();
    }

    public static TrainingType getTrainingTypeStrength() {
        return TrainingType.builder()
                .trainingTypeName(TrainingTypeName.STRENGTH)
                .build();
    }
}

package org.example.utils.converter;

import java.util.List;

import org.example.dto.trainingType.TrainingTypeDTO;
import org.example.model.TrainingType;

public class TrainingTypeConverter {

    private TrainingTypeConverter() {
    }

    public static TrainingTypeDTO convertToDto(TrainingType trainingType) {
        return TrainingTypeDTO.builder()
                .id(trainingType.getId())
                .trainingTypeName(trainingType.getTrainingTypeName().name())
                .build();
    }

    public static List<TrainingTypeDTO> convertToDtoList(List<TrainingType> trainingTypes) {
        return trainingTypes.stream()
                .map(TrainingTypeConverter::convertToDto)
                .toList();
    }
}

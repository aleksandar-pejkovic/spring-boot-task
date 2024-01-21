package org.example.utils.converter;

import java.util.List;

import org.example.dto.training.TrainingDTO;
import org.example.model.Training;

public class TrainingConverter {

    private TrainingConverter() {
    }

    public static List<TrainingDTO> convertToDtoList(List<Training> trainings) {
        return trainings.stream()
                .map(TrainingConverter::convertToDto)
                .toList();
    }

    public static TrainingDTO convertToDto(Training training) {
        return TrainingDTO.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingType(training.getTrainingType().getTrainingTypeName().name())
                .trainingDuration(training.getTrainingDuration())
                .trainerName(retrieveTrainerName(training))
                .build();
    }

    private static String retrieveTrainerName(Training training) {
        return training.getTrainer().getUser().getFirstName()
                + " "
                + training.getTrainer().getUser().getLastName();
    }
}

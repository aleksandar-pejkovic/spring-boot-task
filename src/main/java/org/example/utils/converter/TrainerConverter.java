package org.example.utils.converter;

import java.util.List;

import org.example.dto.trainee.TraineeEmbeddedDTO;
import org.example.dto.trainer.TrainerDTO;
import org.example.dto.trainer.TrainerEmbeddedDTO;
import org.example.model.Trainer;

public class TrainerConverter {

    private TrainerConverter() {
    }

    public static TrainerDTO convertToDto(Trainer trainer) {
        List<TraineeEmbeddedDTO> traineeEmbeddedDTOList =
                TraineeConverter.convertToEmbeddedDtoList(trainer.getTraineeList());

        return TrainerDTO.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization().getTrainingTypeName().name())
                .isActive(trainer.getUser().isActive())
                .traineeEmbeddedDTOList(traineeEmbeddedDTOList)
                .build();
    }

    public static TrainerEmbeddedDTO convertToEmbeddedDto(Trainer trainer) {
        return TrainerEmbeddedDTO.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization().getTrainingTypeName().name())
                .build();
    }

    public static List<TrainerEmbeddedDTO> convertToEmbeddedDtoList(List<Trainer> trainers) {
        return trainers.stream()
                .map(TrainerConverter::convertToEmbeddedDto)
                .toList();
    }
}

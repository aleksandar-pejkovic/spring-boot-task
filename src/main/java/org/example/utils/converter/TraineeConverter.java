package org.example.utils.converter;

import java.util.List;

import org.example.dto.trainee.TraineeDTO;
import org.example.dto.trainee.TraineeEmbeddedDTO;
import org.example.dto.trainer.TrainerEmbeddedDTO;
import org.example.model.Trainee;

public class TraineeConverter {

    private TraineeConverter() {
    }

    public static TraineeDTO convertToDto(Trainee entity) {
        List<TrainerEmbeddedDTO> trainerEmbeddedDTOList = TrainerConverter.convertToEmbeddedDtoList(entity.getTrainerList());

        return TraineeDTO.builder()
                .username(entity.getUsername())
                .firstName(entity.getUser().getFirstName())
                .lastName(entity.getUser().getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .isActive(entity.getUser().isActive())
                .trainerEmbeddedDTOList(trainerEmbeddedDTOList)
                .build();
    }

    public static TraineeEmbeddedDTO convertToEmbeddedDto(Trainee trainee) {
        return TraineeEmbeddedDTO.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .build();
    }

    public static List<TraineeEmbeddedDTO> convertToEmbeddedDtoList(List<Trainee> trainees) {
        return trainees.stream()
                .map(TraineeConverter::convertToEmbeddedDto)
                .toList();
    }
}

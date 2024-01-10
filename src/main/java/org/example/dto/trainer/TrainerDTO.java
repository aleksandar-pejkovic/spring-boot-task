package org.example.dto.trainer;

import java.util.List;

import org.example.dto.trainee.TraineeEmbeddedDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerDTO {

    private String username;

    private String firstName;

    private String lastName;

    private String specialization;

    private boolean isActive;

    private List<TraineeEmbeddedDTO> traineeEmbeddedDTOList;
}

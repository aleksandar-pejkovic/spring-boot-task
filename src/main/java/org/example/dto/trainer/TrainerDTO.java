package org.example.dto.trainer;

import java.util.List;

import org.example.dto.trainee.TraineeEmbeddedDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerDTO {

    private long id;

    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String specialization;

    private boolean isActive;

    @NotNull
    private List<TraineeEmbeddedDTO> traineeEmbeddedDTOList;
}

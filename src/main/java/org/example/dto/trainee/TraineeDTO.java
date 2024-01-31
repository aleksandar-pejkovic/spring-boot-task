package org.example.dto.trainee;

import java.util.Date;
import java.util.List;

import org.example.dto.trainer.TrainerEmbeddedDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraineeDTO {

    private long id;

    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;

    @NotNull
    private List<TrainerEmbeddedDTO> trainerEmbeddedDTOList;
}

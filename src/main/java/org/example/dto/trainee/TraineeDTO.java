package org.example.dto.trainee;

import java.util.Date;
import java.util.List;

import org.example.dto.trainer.TrainerEmbeddedDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraineeDTO {

    private long id;

    private String username;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;

    private List<TrainerEmbeddedDTO> trainerEmbeddedDTOList;
}

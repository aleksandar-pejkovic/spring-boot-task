package org.example.dto.trainer;

import org.example.enums.TrainingTypeName;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerUpdateDTO {

    private String username;

    private String firstName;

    private String lastName;

    private TrainingTypeName specialization;

    private boolean isActive;
}

package org.example.dto.trainer;

import lombok.Builder;

@Builder
public class TrainerEmbeddedDTO {

    private String username;

    private String firstName;

    private String lastName;

    private String specialization;
}

package org.example.dto.trainee;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraineeUpdateDTO {

    @NotNull
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;
}

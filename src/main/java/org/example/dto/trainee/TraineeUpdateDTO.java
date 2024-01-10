package org.example.dto.trainee;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraineeUpdateDTO {

    private String username;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String address;

    private boolean isActive;
}

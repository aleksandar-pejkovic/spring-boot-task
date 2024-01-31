package org.example.dto.trainer;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TrainerListDTO {

    @NotNull
    private List<String> trainerUsernameList;
}

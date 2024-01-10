package org.example.dto.trainer;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerListDTO {

    private List<String> trainerUsernameList;
}

package org.example.dto.credentials;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CredentialsDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;
}

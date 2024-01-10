package org.example.dto.credentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CredentialsDTO {

    private String username;

    private String password;
}

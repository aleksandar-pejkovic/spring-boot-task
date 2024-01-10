package org.example.dto.credentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CredentialsUpdateDTO {

    private String username;

    private String oldPassword;

    private String newPassword;
}

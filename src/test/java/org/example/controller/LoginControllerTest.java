package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.example.dto.credentials.CredentialsDTO;
import org.example.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LoginController.class})
class LoginControllerTest {

    @Autowired
    private LoginController loginController;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void loginSuccess() throws AuthenticationException {
        CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                .username("John.Doe")
                .password("0123456789")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentialsDTO.getUsername(),
                credentialsDTO.getPassword(),
                Collections.singletonList(RoleName.USER)
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        ResponseEntity<Boolean> result = loginController.login(credentialsDTO);

        verify(authenticationManager, times(1)).authenticate(any());
        assertEquals(true, result.getBody());
    }

    @Test
    void loginFailure() throws AuthenticationException {
        CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                .username("John.Doe")
                .password("0123456789")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentialsDTO.getUsername(),
                credentialsDTO.getPassword()
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        ResponseEntity<Boolean> result = loginController.login(credentialsDTO);

        verify(authenticationManager, times(1)).authenticate(any());
        assertEquals(false, result.getBody());
    }
}

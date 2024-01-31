package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.example.dto.credentials.CredentialsDTO;
import org.example.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    private static final String URL_TEMPLATE = "/api/login";
    private static final String DEFAULT_USERNAME = "John.Doe";
    private static final String DEFAULT_PASSWORD = "0123456789";

    private final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    public LoginControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    void loginSuccess() throws Exception {
        CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentialsDTO.getUsername(),
                credentialsDTO.getPassword(),
                Collections.singletonList(RoleName.USER)
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(status().isOk());
    }
}

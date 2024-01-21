package org.example.controller;

import org.example.dto.credentials.CredentialsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@Valid @RequestBody CredentialsDTO credentialsDTO) {
        log.info("Endpoint '/api/login' was called to authenticate trainee");
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                credentialsDTO.getUsername(),
                credentialsDTO.getPassword());

        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        return ResponseEntity.ok(authenticationResponse.isAuthenticated());
    }
}

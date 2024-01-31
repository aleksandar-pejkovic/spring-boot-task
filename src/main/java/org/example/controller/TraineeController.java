package org.example.controller;

import java.util.Date;
import java.util.Optional;

import org.example.dto.credentials.CredentialsDTO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.model.Trainee;
import org.example.service.TraineeService;
import org.example.utils.converter.TraineeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping
    public CredentialsDTO traineeRegistration(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) Date dateOfBirth,
            @RequestParam(required = false) String address
    ) {
        log.info("Endpoint '/api/trainees' was called to register trainee");
        Trainee savedTrainee = traineeService.createTrainee(firstName, lastName, dateOfBirth, address);

        return CredentialsDTO.builder()
                .username(savedTrainee.getUsername())
                .password(savedTrainee.getPassword())
                .build();
    }

    @PutMapping("/change-login")
    public ResponseEntity<Boolean> changeLogin(@Valid @RequestBody CredentialsUpdateDTO credentialsUpdateDTO) {
        log.info("Endpoint '/api/trainees/change-login' was called to update trainee's credentials");
        Trainee traineeAfterUpdate = traineeService.changePassword(credentialsUpdateDTO);
        return Optional.ofNullable(traineeAfterUpdate).isPresent()
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/{username}")
    public TraineeDTO getTraineeByUsername(@PathVariable String username) {
        log.info("Endpoint '/api/trainees/{username}' was called to get trainee by username");
        Trainee trainee = traineeService.getTraineeByUsername(username);
        return TraineeConverter.convertToDto(trainee);
    }

    @PutMapping
    public TraineeDTO updateTraineeProfile(@Valid @RequestBody TraineeUpdateDTO traineeUpdateDTO) {
        log.info("Endpoint '/api/trainees' was called to update trainee profile");
        Trainee updatedTrainee = traineeService.updateTrainee(traineeUpdateDTO);
        return TraineeConverter.convertToDto(updatedTrainee);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteTraineeProfile(@RequestParam String username) {
        log.info("Endpoint '/api/trainees' was called to delete trainee profile");
        boolean successfulDeletion = traineeService.deleteTrainee(username);
        return successfulDeletion
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping
    public ResponseEntity<Boolean> toggleTraineeActivation(@RequestParam String username,
                                                           @RequestParam boolean isActive) {
        log.info("Endpoint '/api/trainees' was called to toggle trainee's activation status");
        boolean successfulRequest = traineeService.toggleTraineeActivation(username, isActive);
        return successfulRequest
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }
}

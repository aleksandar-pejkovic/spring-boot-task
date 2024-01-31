package org.example.controller;

import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsDTO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerDTO;
import org.example.dto.trainer.TrainerEmbeddedDTO;
import org.example.dto.trainer.TrainerListDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainer;
import org.example.service.TrainerService;
import org.example.utils.converter.TrainerConverter;
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
@RequestMapping(value = "/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public CredentialsDTO traineeRegistration(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam TrainingTypeName specialization
    ) {
        log.info("Endpoint '/api/trainers' was called to register trainer profile");
        Trainer savedTrainer = trainerService.createTrainer(firstName, lastName, specialization);

        return CredentialsDTO.builder()
                .username(savedTrainer.getUsername())
                .password(savedTrainer.getPassword())
                .build();
    }

    @PutMapping("/change-login")
    public ResponseEntity<Boolean> changeLogin(@Valid @RequestBody CredentialsUpdateDTO credentialsUpdateDTO) {
        log.info("Endpoint '/api/trainers/change-login' was called to update trainers credentials");
        Trainer trainerAfterUpdate = trainerService.changePassword(credentialsUpdateDTO);
        return Optional.ofNullable(trainerAfterUpdate).isPresent()
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/{username}")
    public TrainerDTO getTrainerByUsername(@PathVariable String username) {
        log.info("Endpoint '/api/trainers/{username}' was called to get trainer by username");
        Trainer trainer = trainerService.getTrainerByUsername(username);
        return TrainerConverter.convertToDto(trainer);
    }

    @PutMapping
    public TrainerDTO updateTrainerProfile(@Valid @RequestBody TrainerUpdateDTO trainerUpdateDTO) {
        log.info("Endpoint '/api/trainers' was called to update trainer profile");
        Trainer updatedTrainer = trainerService.updateTrainer(trainerUpdateDTO);
        return TrainerConverter.convertToDto(updatedTrainer);
    }

    @GetMapping("/unassigned")
    public List<TrainerEmbeddedDTO> getNotAssignedOnTrainee(@RequestParam String traineeUsername) {
        log.info("Endpoint '/api/trainers/unassigned' was called to get a list of unassigned trainers");
        List<Trainer> unassignedTrainers = trainerService.getNotAssignedTrainerList(traineeUsername);
        return TrainerConverter.convertToEmbeddedDtoList(unassignedTrainers);
    }

    @PutMapping("/{traineeUsername}/updateTrainers")
    public List<TrainerEmbeddedDTO> updateTraineeTrainerList(
            @PathVariable String traineeUsername,
            @Valid @RequestBody TrainerListDTO trainerListDTO
    ) {
        log.info("Endpoint '/api/trainers/{traineeUsername}/updateTrainers' was called to update trainee's trainer "
                + "list");
        List<Trainer> updatedTraineeTrainerList = trainerService.updateTraineeTrainerList(traineeUsername, trainerListDTO);
        return TrainerConverter.convertToEmbeddedDtoList(updatedTraineeTrainerList);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteTraineeProfile(@RequestParam String username) {
        log.info("Endpoint '/api/trainers' was called to delete trainer profile");
        boolean successfulDeletion = trainerService.deleteTrainer(username);
        return successfulDeletion
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping
    public ResponseEntity<Boolean> toggleTraineeActivation(@RequestParam String username,
                                                           @RequestParam boolean isActive) {
        log.info("Endpoint '/api/trainees' was called to toggle trainer's activation status");
        boolean successfulRequest = trainerService.toggleTrainerActivation(username, isActive);
        return successfulRequest
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }
}

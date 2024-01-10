package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.credentials.CredentialsDTO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerDTO;
import org.example.dto.trainer.TrainerEmbeddedDTO;
import org.example.dto.trainer.TrainerListDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrainerController.class})
class TrainerControllerTest {

    @Autowired
    private TrainerController trainerController;

    @MockBean
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .isActive(true)
                .lastName("Doe")
                .firstName("John")
                .username("John.Doe")
                .password("0123456789")
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .trainerList(new ArrayList<>())
                .build();

        trainer = Trainer.builder()
                .specialization(trainingType)
                .user(user)
                .traineeList(new ArrayList<>())
                .trainingList(new ArrayList<>())
                .build();
    }

    @Test
    void traineeRegistration() {
        TrainingTypeName specialization = TrainingTypeName.AEROBIC;

        when(trainerService.createTrainer(anyString(), anyString(), any())).thenReturn(trainer);

        CredentialsDTO result = trainerController.traineeRegistration("John", "Doe", specialization);

        verify(trainerService, times(1)).createTrainer("John", "Doe", specialization);
        assertEquals("John.Doe", result.getUsername());
        assertEquals("0123456789", result.getPassword());
    }

    @Test
    void changeLogin() {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("John.Doe")
                .oldPassword("0123456789")
                .newPassword("0123456789")
                .build();

        when(trainerService.changePassword(any())).thenReturn(trainer);

        ResponseEntity<Boolean> result = trainerController.changeLogin(credentialsUpdateDTO);

        verify(trainerService, times(1)).changePassword(credentialsUpdateDTO);
        assertEquals(true, result.getBody());
    }

    @Test
    void getTrainerByUsername() {
        String username = "John.Doe";
        when(trainerService.getTrainerByUsername(username)).thenReturn(trainer);

        TrainerDTO result = trainerController.getTrainerByUsername(username);

        verify(trainerService, times(1)).getTrainerByUsername(username);
        assertEquals(trainer.getUsername(), result.getUsername());
    }

    @Test
    void updateTrainerProfile() {
        TrainerUpdateDTO trainerUpdateDTO = TrainerUpdateDTO.builder().build();

        when(trainerService.updateTrainer(any())).thenReturn(trainer);

        TrainerDTO result = trainerController.updateTrainerProfile(trainerUpdateDTO);

        verify(trainerService, times(1)).updateTrainer(trainerUpdateDTO);
        assertEquals(trainer.getUsername(), result.getUsername());
    }

    @Test
    void getNotAssignedOnTrainee() {
        String traineeUsername = "Trainee1";
        List<Trainer> unassignedTrainers = new ArrayList<>();
        when(trainerService.getNotAssignedTrainerList(traineeUsername)).thenReturn(unassignedTrainers);

        List<TrainerEmbeddedDTO> result = trainerController.getNotAssignedOnTrainee(traineeUsername);

        verify(trainerService, times(1)).getNotAssignedTrainerList(traineeUsername);
        assertEquals(0, result.size());
    }

    @Test
    void updateTraineeTrainerList() {
        String traineeUsername = "Trainee1";
        TrainerListDTO trainerListDTO = TrainerListDTO.builder().build();
        List<Trainer> updatedTraineeTrainerList = new ArrayList<>();
        when(trainerService.updateTraineeTrainerList(traineeUsername, trainerListDTO))
                .thenReturn(updatedTraineeTrainerList);

        List<TrainerEmbeddedDTO> result = trainerController.updateTraineeTrainerList(traineeUsername, trainerListDTO);

        verify(trainerService, times(1)).updateTraineeTrainerList(traineeUsername, trainerListDTO);
        assertEquals(0, result.size());
    }

    @Test
    void toggleTraineeActivation() {
        String username = "John.Doe";
        boolean isActive = true;
        when(trainerService.toggleTrainerActivation(username, isActive)).thenReturn(true);

        ResponseEntity<Boolean> result = trainerController.toggleTraineeActivation(username, isActive);

        verify(trainerService, times(1)).toggleTrainerActivation(username, isActive);
        assertEquals(true, result.getBody());
    }
}

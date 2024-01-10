package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import org.example.dto.credentials.CredentialsDTO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class TraineeControllerTest {

    @InjectMocks
    private TraineeController traineeController;

    @Mock
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() throws Exception {
        try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
            User user = User.builder()
                    .isActive(true)
                    .lastName("Doe")
                    .firstName("John")
                    .username("John.Doe")
                    .password("0123456789")
                    .build();

            trainee = Trainee.builder()
                    .user(user)
                    .address("11000 Belgrade")
                    .dateOfBirth(new Date())
                    .trainerList(new ArrayList<>())
                    .build();
        }
    }

    @Test
    void traineeRegistration() {
        Date date = new Date();

        when(traineeService.createTrainee(anyString(), anyString(), any(), anyString())).thenReturn(trainee);

        CredentialsDTO result = traineeController.traineeRegistration("John", "Doe", date, "Random 17");

        verify(traineeService, times(1)).createTrainee("John", "Doe", date, "Random 17");
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

        when(traineeService.changePassword(any())).thenReturn(trainee);

        ResponseEntity<Boolean> result = traineeController.changeLogin(credentialsUpdateDTO);

        verify(traineeService, times(1)).changePassword(credentialsUpdateDTO);
        assertEquals(true, result.getBody());
    }

    @Test
    void getTraineeByUsername() {
        String username = "John.Doe";
        when(traineeService.getTraineeByUsername(anyString())).thenReturn(trainee);

        TraineeDTO result = traineeController.getTraineeByUsername(username);

        verify(traineeService, times(1)).getTraineeByUsername(username);
        assertEquals(trainee.getUser().getFirstName(), result.getFirstName());
    }

    @Test
    void updateTraineeProfile() {
        TraineeUpdateDTO traineeUpdateDTO = TraineeUpdateDTO.builder().build();

        when(traineeService.updateTrainee(any())).thenReturn(trainee);

        TraineeDTO result = traineeController.updateTraineeProfile(traineeUpdateDTO);

        verify(traineeService, times(1)).updateTrainee(traineeUpdateDTO);
        assertEquals(trainee.getUser().getFirstName(), result.getFirstName());
    }

    @Test
    void deleteTraineeProfile() {
        String username = "John.Doe";
        when(traineeService.deleteTrainee(username)).thenReturn(true);

        ResponseEntity<Boolean> result = traineeController.deleteTraineeProfile(username);

        verify(traineeService, times(1)).deleteTrainee(username);
        assertEquals(true, result.getBody());
    }

    @Test
    void toggleTraineeActivation() {
        String username = "John.Doe";
        boolean isActive = true;
        when(traineeService.toggleTraineeActivation(username, isActive)).thenReturn(true);

        ResponseEntity<Boolean> result = traineeController.toggleTraineeActivation(username, isActive);

        verify(traineeService, times(1)).toggleTraineeActivation(username, isActive);
        assertEquals(true, result.getBody());
    }
}

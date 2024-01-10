package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.example.dao.TraineeDAO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() throws Exception {
        try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
            User user = User.builder()
                    .isActive(true)
                    .lastName("Biaggi")
                    .firstName("Max")
                    .username("Max.Biaggi")
                    .password("0123456789")
                    .build();

            trainee = Trainee.builder()
                    .user(user)
                    .address("11000 Belgrade")
                    .dateOfBirth(new Date())
                    .build();
        }
    }

    @Test
    void createTrainee() {
        // Arrange
        when(credentialsGenerator.generateUsername(any())).thenReturn("Max.Biaggi");
        when(credentialsGenerator.generateRandomPassword()).thenReturn("0123456789");
        when(traineeDAO.saveTrainee(any())).thenReturn(trainee);

        // Act
        Trainee result = traineeService.createTrainee(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );

        // Assert
        verify(traineeDAO, times(1)).saveTrainee(any());
        assertEquals("Max.Biaggi", result.getUsername());
        assertEquals("0123456789", result.getPassword());
    }

    @Test
    void getTraineeByUsername() {
        // Arrange
        String username = "testUser";
        Trainee expectedTrainee = new Trainee();
        when(traineeDAO.findTraineeByUsername(username)).thenReturn(expectedTrainee);

        // Act
        Trainee result = traineeService.getTraineeByUsername(username);

        // Assert
        verify(traineeDAO, times(1)).findTraineeByUsername(username);
        assertEquals(expectedTrainee, result);
    }

    @Test
    void changePassword() {
        // Arrange
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        when(traineeService.getTraineeByUsername(credentialsUpdateDTO.getUsername())).thenReturn(trainee);
        when(traineeDAO.updateTrainee(trainee)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.changePassword(credentialsUpdateDTO);

        // Assert
        verify(traineeDAO, times(1)).updateTrainee(trainee);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    void updateTrainee() {
        // Arrange
        when(traineeDAO.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(traineeDAO.updateTrainee(trainee)).thenReturn(trainee);

        // Act
        TraineeUpdateDTO traineeUpdateDTO = TraineeUpdateDTO.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .build();
        Trainee result = traineeService.updateTrainee(traineeUpdateDTO);

        // Assert
        verify(traineeDAO, times(1)).updateTrainee(trainee);
        assertEquals(trainee, result);
    }

    @Test
    void toggleTraineeActivationTest() {
        // Arrange
        when(traineeDAO.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(traineeDAO.updateTrainee(trainee)).thenReturn(trainee);

        // Act
        boolean result = traineeService.toggleTraineeActivation(trainee.getUsername(), trainee.getUser().isActive());

        // Assert
        verify(traineeDAO, times(1)).updateTrainee(trainee);
        assertTrue(result);
    }

    @Test
    void deleteTrainee() {
        // Arrange
        String username = "testUser";
        when(traineeDAO.deleteTraineeByUsername(username)).thenReturn(true);

        // Act
        boolean result = traineeService.deleteTrainee(username);

        // Assert
        verify(traineeDAO, times(1)).deleteTraineeByUsername(username);
        assertTrue(result);
    }

    @Test
    void getAllTrainees() {
        // Arrange
        List<Trainee> expectedTrainees = Collections.singletonList(new Trainee());
        when(traineeDAO.getAllTrainees()).thenReturn(expectedTrainees);

        // Act
        List<Trainee> result = traineeService.getAllTrainees();

        // Assert
        verify(traineeDAO, times(1)).getAllTrainees();
        assertEquals(expectedTrainees, result);
    }
}

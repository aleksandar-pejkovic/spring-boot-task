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

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.TraineeRepository;
import org.example.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TraineeService.class})
class TraineeServiceTest {

    @MockBean
    private TraineeRepository traineeRepository;

    @MockBean
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() throws Exception {
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

    @Test
    void createTrainee() {
        // Arrange
        when(credentialsGenerator.generateUsername(any())).thenReturn("Max.Biaggi");
        when(credentialsGenerator.generateRandomPassword()).thenReturn("0123456789");
        when(traineeRepository.save(any())).thenReturn(trainee);

        // Act
        Trainee result = traineeService.createTrainee(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress()
        );

        // Assert
        verify(traineeRepository, times(1)).save(any());
        assertEquals("Max.Biaggi", result.getUsername());
        assertEquals("0123456789", result.getPassword());
    }

    @Test
    void getTraineeByUsername() {
        // Arrange
        String username = "testUser";
        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.findTraineeByUsername(username)).thenReturn(expectedTrainee);

        // Act
        Trainee result = traineeService.getTraineeByUsername(username);

        // Assert
        verify(traineeRepository, times(1)).findTraineeByUsername(username);
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
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        Trainee result = traineeService.changePassword(credentialsUpdateDTO);

        // Assert
        verify(traineeRepository, times(1)).save(trainee);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    void updateTrainee() {
        // Arrange
        when(traineeRepository.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

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
        verify(traineeRepository, times(1)).save(trainee);
        assertEquals(trainee, result);
    }

    @Test
    void toggleTraineeActivationTest() {
        // Arrange
        when(traineeRepository.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        boolean result = traineeService.toggleTraineeActivation(trainee.getUsername(), trainee.getUser().isActive());

        // Assert
        verify(traineeRepository, times(1)).save(trainee);
        assertTrue(result);
    }

    @Test
    void deleteTrainee() {
        // Arrange
        String username = "testUser";
        when(traineeRepository.deleteTraineeByUsername(username)).thenReturn(true);

        // Act
        boolean result = traineeService.deleteTrainee(username);

        // Assert
        verify(traineeRepository, times(1)).deleteTraineeByUsername(username);
        assertTrue(result);
    }

    @Test
    void getAllTrainees() {
        // Arrange
        List<Trainee> expectedTrainees = Collections.singletonList(new Trainee());
        when(traineeRepository.findAll()).thenReturn(expectedTrainees);

        // Act
        List<Trainee> result = traineeService.getAllTrainees();

        // Assert
        verify(traineeRepository, times(1)).findAll();
        assertEquals(expectedTrainees, result);
    }
}

package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TraineeNotFoundException;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.TraineeRepository;
import org.example.utils.credentials.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        Trainee expectedTrainee = new Trainee();
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(expectedTrainee));

        // Act
        Trainee result = traineeService.getTraineeByUsername("username");

        // Assert
        verify(traineeRepository, times(1)).findByUserUsername("username");
        assertEquals(expectedTrainee, result);
    }

    @Test
    @DisplayName("getTraineeByUsername throws TraineeNotFoundException when Trainee is not found")
    void getTraineeByUsernameThrowsTraineeNotFoundExceptionWhenTraineeNotFound() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> traineeService.getTraineeByUsername("username"));

        verify(traineeRepository, times(1)).findByUserUsername("username");
    }

    @Test
    void changePassword() {
        // Arrange
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("0123456789")
                .newPassword("newPassword")
                .build();

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);

        // Act
        Trainee result = traineeService.changePassword(credentialsUpdateDTO);

        // Assert
        verify(traineeRepository, times(1)).save(trainee);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    @DisplayName("changePassword throws IncorrectPasswordException when old password is incorrect")
    void changePasswordThrowsIncorrectPasswordExceptionWhenOldPasswordIsWrong() {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("wrongOldPassword")
                .newPassword("newPassword")
                .build();

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainee));

        assertThrows(IncorrectPasswordException.class, () -> traineeService.changePassword(credentialsUpdateDTO));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword throws IdenticalPasswordException when new password is the same as old password")
    void changePasswordThrowsIdenticalPasswordExceptionWhenNewPasswordEqualsOldPassword() {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("0123456789")
                .newPassword("0123456789")
                .build();

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainee));

        assertThrows(IdenticalPasswordException.class, () -> traineeService.changePassword(credentialsUpdateDTO));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTrainee() {
        // Arrange
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainee));
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
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Act
        boolean result = traineeService.toggleTraineeActivation(trainee.getUsername(), trainee.getUser().isActive());

        // Assert
        verify(traineeRepository, times(1)).save(trainee);
        assertTrue(result);
    }

    @Test
    @DisplayName("toggleTraineeActivation throws TraineeNotFoundException when Trainee is not found")
    void toggleTraineeActivationThrowsTraineeNotFoundExceptionWhenUserNotFound() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> traineeService.toggleTraineeActivation("Bad.Username", true));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deleteTrainee() {
        // Arrange
        String username = "testUser";
        when(traineeRepository.deleteByUserUsername(username)).thenReturn(true);

        // Act
        boolean result = traineeService.deleteTrainee(username);

        // Assert
        verify(traineeRepository, times(1)).deleteByUserUsername(username);
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

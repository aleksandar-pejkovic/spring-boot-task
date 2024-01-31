package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TraineeNotFoundException;
import org.example.model.Trainee;
import org.example.repository.TraineeRepository;
import org.example.utils.credentials.CredentialsGenerator;
import org.example.utils.dummydata.TraineeDummyDataFactory;
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

    private static final String USERNAME = "John.Doe";
    private static final String PASSWORD = "0123456789";
    private static final String BAD_USERNAME = "Bad.Username";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String WRONG_OLD_PASSWORD = "wrongOldPassword";
    private static final boolean IS_ACTIVE = true;

    @MockBean
    private TraineeRepository traineeRepository;

    @MockBean
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    private TraineeService traineeService;

    private Trainee traineeUnderTest;

    @BeforeEach
    void setUp() {
        traineeUnderTest = TraineeDummyDataFactory.getTraineeUnderTestJohnDoe();
    }

    @Test
    @DisplayName("Should return Trainee when createTrainee")
    void shouldReturnTraineeWhenCreateTrainee() {
        when(credentialsGenerator.generateUsername(any())).thenReturn(USERNAME);
        when(credentialsGenerator.generateRandomPassword()).thenReturn(PASSWORD);
        when(traineeRepository.save(any())).thenReturn(traineeUnderTest);

        Trainee result = traineeService.createTrainee(
                traineeUnderTest.getUser().getFirstName(),
                traineeUnderTest.getUser().getLastName(),
                traineeUnderTest.getDateOfBirth(),
                traineeUnderTest.getAddress()
        );

        verify(traineeRepository).save(any());
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    @DisplayName("Should return Trainee when getTraineeByUsername")
    void shouldReturnTraineeWhenGetTraineeByUsername() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(traineeUnderTest));

        Trainee result = traineeService.getTraineeByUsername(USERNAME);

        verify(traineeRepository).findByUserUsername(USERNAME);
        assertEquals(traineeUnderTest, result);
    }

    @Test
    @DisplayName("Should throw TraineeNotFoundException for invalid username when getTraineeByUsername")
    void shouldThrowTraineeNotFoundExceptionForInvalidUsernameWhenGetTraineeByUsername() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> traineeService.getTraineeByUsername(BAD_USERNAME));

        verify(traineeRepository).findByUserUsername(BAD_USERNAME);
    }

    @Test
    @DisplayName("Should return Trainee when changePassword")
    void shouldReturnTraineeWhenChangePassword() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(PASSWORD, NEW_PASSWORD);

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(traineeUnderTest));
        when(traineeRepository.save(any())).thenReturn(traineeUnderTest);

        Trainee result = traineeService.changePassword(credentialsUpdateDTO);

        verify(traineeRepository).save(traineeUnderTest);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should throw IncorrectPasswordException for incorrect old password")
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordIsIncorrect() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(WRONG_OLD_PASSWORD, NEW_PASSWORD);

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(traineeUnderTest));

        assertThrows(IncorrectPasswordException.class, () -> traineeService.changePassword(credentialsUpdateDTO));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IdenticalPasswordException when old password equals new password")
    void shouldThrowIdenticalPasswordExceptionWhenOldPasswordEqualsNewPassword() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(PASSWORD, PASSWORD);

        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(traineeUnderTest));

        assertThrows(IdenticalPasswordException.class, () -> traineeService.changePassword(credentialsUpdateDTO));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return Trainee when updateTrainee")
    void shouldReturnTraineeWhenUpdateTrainee() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(traineeUnderTest));
        when(traineeRepository.save(traineeUnderTest)).thenReturn(traineeUnderTest);

        TraineeUpdateDTO traineeUpdateDTO = createTraineeUpdateDTO();
        Trainee result = traineeService.updateTrainee(traineeUpdateDTO);

        verify(traineeRepository).save(traineeUnderTest);
        assertEquals(traineeUnderTest, result);
    }

    @Test
    @DisplayName("Should return true when toggleTraineeActivation")
    void shouldReturnTrueWhenToggleTraineeActivation() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(traineeUnderTest));
        when(traineeRepository.save(traineeUnderTest)).thenReturn(traineeUnderTest);

        boolean result = traineeService.toggleTraineeActivation(traineeUnderTest.getUsername(), traineeUnderTest.getUser().isActive());

        verify(traineeRepository).save(traineeUnderTest);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw TraineeNotFoundException for invalid username in toggleTraineeActivation")
    void shouldThrowTraineeNotFoundExceptionForInvalidUsernameWhenToggleTraineeActivation() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> traineeService.toggleTraineeActivation(BAD_USERNAME, IS_ACTIVE));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when deleteTrainee")
    void shouldReturnTrueWhenDeleteTrainee() {
        String username = traineeUnderTest.getUsername();
        when(traineeRepository.deleteByUserUsername(anyString())).thenReturn(IS_ACTIVE);

        boolean result = traineeService.deleteTrainee(username);

        verify(traineeRepository).deleteByUserUsername(username);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return list of Trainees when getAllTrainees")
    void shouldReturnTraineeListWhenGetAllTrainees() {
        List<Trainee> expectedTrainees = Collections.singletonList(new Trainee());
        when(traineeRepository.findAll()).thenReturn(expectedTrainees);

        List<Trainee> result = traineeService.getAllTrainees();

        verify(traineeRepository).findAll();
        assertEquals(expectedTrainees, result);
    }

    private CredentialsUpdateDTO createCredentialsUpdateDTO(String oldPassword,
                                                            String newPassword) {
        return CredentialsUpdateDTO.builder()
                .username(traineeUnderTest.getUsername())
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }

    private TraineeUpdateDTO createTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .username(traineeUnderTest.getUsername())
                .firstName(traineeUnderTest.getUser().getFirstName())
                .lastName(traineeUnderTest.getUser().getLastName())
                .dateOfBirth(traineeUnderTest.getDateOfBirth())
                .address(traineeUnderTest.getAddress())
                .isActive(traineeUnderTest.getUser().isActive())
                .build();
    }
}

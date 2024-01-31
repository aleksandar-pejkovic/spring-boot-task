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
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TrainerNotFoundException;
import org.example.exception.notfound.TrainingTypeNotFoundException;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.utils.credentials.CredentialsGenerator;
import org.example.utils.dummydata.TrainerDummyDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrainerService.class})
class TrainerServiceTest {

    public static final String USERNAME = "Joe.Johnson";
    public static final String PASSWORD = "0123456789";
    public static final String WRONG_OLD_PASSWORD = "wrongOldPassword";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String BAD_USERNAME = "Bad.Username";
    public static final boolean IS_ACTIVE = true;

    @MockBean
    private TrainerRepository trainerRepository;

    @MockBean
    private TraineeRepository traineeRepository;

    @MockBean
    private TrainingTypeRepository trainingTypeRepository;

    @MockBean
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    private TrainerService trainerService;

    private Trainer trainerUnderTest;

    @BeforeEach
    void setUp() {
        trainerUnderTest = TrainerDummyDataFactory.getTrainerUnderTestingJoeJohnson();
    }

    @Test
    @DisplayName("Should return Trainer when createTrainer")
    void shouldReturnTrainerWhenCreateTrainer() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(credentialsGenerator.generateUsername(any())).thenReturn(USERNAME);
        when(credentialsGenerator.generateRandomPassword()).thenReturn(PASSWORD);
        when(trainerRepository.save(any())).thenReturn(trainerUnderTest);

        Trainer result = trainerService.createTrainer(trainerUnderTest.getUser().getFirstName(),
                trainerUnderTest.getUser().getLastName(), trainerUnderTest.getSpecialization().getTrainingTypeName());

        verify(trainerRepository).save(any());
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    @DisplayName("Should return Trainee when getTraineeByUsername")
    void shouldReturnTrainerWhenGetTrainerByUsername() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainerUnderTest));

        Trainer result = trainerService.getTrainerByUsername(USERNAME);

        verify(trainerRepository).findByUserUsername(anyString());
        assertEquals(trainerUnderTest, result);
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException for invalid username when getTrainerByUsername")
    void shouldThrowTrainerNotFoundExceptionForInvalidUsernameWhenGetTrainerByUsername() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.getTrainerByUsername(USERNAME));

        verify(trainerRepository).findByUserUsername(USERNAME);
    }

    @Test
    @DisplayName("Should return Trainee when changePassword")
    void shouldReturnTrainerWhenChangePassword() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(PASSWORD, NEW_PASSWORD);

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainerUnderTest));
        when(trainerRepository.save(trainerUnderTest)).thenReturn(trainerUnderTest);

        Trainer result = trainerService.changePassword(credentialsUpdateDTO);

        verify(trainerRepository).save(trainerUnderTest);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should throw IncorrectPasswordException for incorrect old password")
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordIsIncorrect() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(WRONG_OLD_PASSWORD, NEW_PASSWORD);

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainerUnderTest));

        assertThrows(IncorrectPasswordException.class, () -> trainerService.changePassword(credentialsUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IdenticalPasswordException when old password equals new password")
    void shouldThrowIdenticalPasswordExceptionWhenOldPasswordEqualsNewPassword() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO(PASSWORD, PASSWORD);

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainerUnderTest));

        assertThrows(IdenticalPasswordException.class, () -> trainerService.changePassword(credentialsUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return Trainer when updateTrainer")
    void shouldReturnTrainerWhenUpdateTrainer() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainerUnderTest));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(trainerUnderTest)).thenReturn(trainerUnderTest);
        TrainerUpdateDTO trainerUpdateDTO = createTrainerUpdateDTO();

        trainerService.updateTrainer(trainerUpdateDTO);

        verify(trainerRepository).save(trainerUnderTest);
    }

    @Test
    @DisplayName("Should throw TrainingTypeNotFoundException for incorrect TrainingType when updateTrainer")
    void shouldThrowTrainingTypeNotFoundExceptionForIncorrectTrainingTypeNameWhenUpdateTrainer() {
        TrainerUpdateDTO trainerUpdateDTO = createTrainerUpdateDTO();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainerUnderTest));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () -> trainerService.updateTrainer(trainerUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when toggleTrainerActivation")
    void shouldReturnTrueWhenToggleTrainerActivation() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainerUnderTest));
        when(trainerRepository.save(trainerUnderTest)).thenReturn(trainerUnderTest);

        boolean result = trainerService.toggleTrainerActivation(trainerUnderTest.getUsername(), trainerUnderTest.getUser().isActive());

        verify(trainerRepository).save(trainerUnderTest);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException for invalid username in toggleTrainerActivation")
    void shouldThrowTrainerNotFoundExceptionForInvalidUsernameWhenToggleTrainerActivation() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.toggleTrainerActivation(BAD_USERNAME, IS_ACTIVE));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when deleteTrainer")
    void shouldReturnTrueWhenDeleteTrainer() {
        String username = trainerUnderTest.getUsername();
        when(trainerRepository.deleteByUserUsername(username)).thenReturn(IS_ACTIVE);

        boolean result = trainerService.deleteTrainer(username);

        verify(trainerRepository).deleteByUserUsername(username);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return list of trainers when getNotAssignedTrainerList")
    void shouldReturnTrainerListWhenGetNotAssignedTrainerList() {
        String traineeUsername = trainerUnderTest.getUsername();
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername)).thenReturn(expectedTrainers);

        List<Trainer> result = trainerService.getNotAssignedTrainerList(traineeUsername);

        verify(trainerRepository).findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername);
        assertEquals(expectedTrainers, result);
    }

    @Test
    @DisplayName("Should return list of trainers when getAllTrainers")
    void shouldReturnTrainerListWhenGetAllTrainers() {
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findAll()).thenReturn(expectedTrainers);

        List<Trainer> result = trainerService.getAllTrainers();

        verify(trainerRepository).findAll();
        assertEquals(expectedTrainers, result);
    }

    private CredentialsUpdateDTO createCredentialsUpdateDTO(String oldPassword,
                                                            String newPassword) {
        return CredentialsUpdateDTO.builder()
                .username(trainerUnderTest.getUsername())
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }

    private TrainerUpdateDTO createTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(trainerUnderTest.getUsername())
                .firstName(trainerUnderTest.getUser().getFirstName())
                .lastName(trainerUnderTest.getUser().getLastName())
                .specialization(trainerUnderTest.getSpecialization().getTrainingTypeName())
                .isActive(trainerUnderTest.getUser().isActive())
                .build();
    }
}
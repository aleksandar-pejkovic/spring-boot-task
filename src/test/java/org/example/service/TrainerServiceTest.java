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
import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TrainerNotFoundException;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
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
@ContextConfiguration(classes = {TrainerService.class})
class TrainerServiceTest {

    @MockBean
    private TrainerRepository trainerRepository;

    @MockBean
    private TrainingTypeRepository trainingTypeRepository;

    @MockBean
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .isActive(true)
                .lastName("Rossi")
                .firstName("Valentino")
                .username("Valentino.Rossi")
                .password("9876543210")
                .build();

        trainer = Trainer.builder()
                .user(user)
                .specialization(TrainingType.builder()
                        .id(1L)
                        .trainingTypeName(TrainingTypeName.AEROBIC)
                        .build())
                .build();
    }

    @Test
    void createTrainer() {
        // Arrange
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(credentialsGenerator.generateUsername(any())).thenReturn("Valentino.Rossi");
        when(credentialsGenerator.generateRandomPassword()).thenReturn("9876543210");
        when(trainerRepository.save(any())).thenReturn(trainer);

        // Act
        Trainer result = trainerService.createTrainer(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName());

        // Assert
        verify(trainerRepository, times(1)).save(any());
    }

    @Test
    void getTrainerByUsername() {
        // Arrange
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));

        // Act
        Trainer result = trainerService.getTrainerByUsername("username");

        // Assert
        verify(trainerRepository, times(1)).findByUserUsername(anyString());
        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("getTrainerByUsername throws TrainerNotFoundException when Trainer is not found")
    void getTrainerByUsernameThrowsTrainerNotFoundExceptionWhenTrainerIsNotFound() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.getTrainerByUsername("username"));

        verify(trainerRepository, times(1)).findByUserUsername("username");
    }

    @Test
    void changePassword() {
        // Arrange
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.changePassword(credentialsUpdateDTO);

        // Assert
        verify(trainerRepository, times(1)).save(trainer);
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

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));

        assertThrows(IncorrectPasswordException.class, () -> trainerService.changePassword(credentialsUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword teturns IdenticalPasswordException when new password is the same as old password")
    void changePasswordThrowsIdenticalPasswordExceptionWhenNewPasswordEqualsOldPassword() {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("0123456789")
                .newPassword("0123456789")
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));

        assertThrows(IdenticalPasswordException.class, () -> trainerService.changePassword(credentialsUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainer() {
        // Arrange
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        TrainerUpdateDTO trainerUpdateDTO = TrainerUpdateDTO.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization().getTrainingTypeName())
                .isActive(trainer.getUser().isActive())
                .build();

        // Act
        trainerService.updateTrainer(trainerUpdateDTO);

        // Assert
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    @DisplayName("updateTrainer throws TrainingTypeNotFoundException when TrainingType is not found")
    void updateTrainerThrowsTrainingTypeNotFoundExceptionWhenTrainingTypeNotFound() {
        TrainerUpdateDTO trainerUpdateDTO = TrainerUpdateDTO.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization().getTrainingTypeName())
                .isActive(trainer.getUser().isActive())
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.updateTrainer(trainerUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void toggleTrainerActivationTest() {
        // Arrange
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Act
        boolean result = trainerService.toggleTrainerActivation(trainer.getUsername(), trainer.getUser().isActive());

        // Assert
        verify(trainerRepository, times(1)).save(trainer);
        assertTrue(result);
    }

    @Test
    @DisplayName("toggleTrainerActivation throws TrainerNotFoundException when Trainer is not found")
    void toggleTraineeActivationThrowsTraineeNotFoundExceptionWhenUserNotFound() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.toggleTrainerActivation("Bad.Username", true));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void deleteTrainer() {
        // Arrange
        String username = "testUser";
        when(trainerRepository.deleteByUserUsername(username)).thenReturn(true);

        // Act
        boolean result = trainerService.deleteTrainer(username);

        // Assert
        verify(trainerRepository, times(1)).deleteByUserUsername(username);
        assertTrue(result);
    }

    @Test
    void getNotAssignedTrainerList() {
        // Arrange
        String traineeUsername = "traineeUser";
        String password = "testPassword";
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername)).thenReturn(expectedTrainers);

        // Act
        List<Trainer> result = trainerService.getNotAssignedTrainerList(traineeUsername);

        // Assert
        verify(trainerRepository, times(1)).findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername);
        assertEquals(expectedTrainers, result);
    }

    @Test
    void getAllTrainers() {
        // Arrange
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findAll()).thenReturn(expectedTrainers);

        // Act
        List<Trainer> result = trainerService.getAllTrainers();

        // Assert
        verify(trainerRepository, times(1)).findAll();
        assertEquals(expectedTrainers, result);
    }

}
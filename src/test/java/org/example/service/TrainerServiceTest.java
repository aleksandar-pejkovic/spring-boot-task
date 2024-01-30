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
                .password("0123456789")
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
    @DisplayName("Should return Trainer when createTrainer")
    void shouldReturnTrainerWhenCreateTrainer() {
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(credentialsGenerator.generateUsername(any())).thenReturn("Valentino.Rossi");
        when(credentialsGenerator.generateRandomPassword()).thenReturn("9876543210");
        when(trainerRepository.save(any())).thenReturn(trainer);

        Trainer result = trainerService.createTrainer(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName());

        verify(trainerRepository, times(1)).save(any());
        assertEquals("Valentino.Rossi", result.getUsername());
        assertEquals("0123456789", result.getPassword());
    }

    @Test
    @DisplayName("Should return Trainee when getTraineeByUsername")
    void shouldReturnTrainerWhenGetTrainerByUsername() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerByUsername("username");

        verify(trainerRepository, times(1)).findByUserUsername(anyString());
        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException for invalid username when getTrainerByUsername")
    void shouldThrowTrainerNotFoundExceptionForInvalidUsernameWhenGetTrainerByUsername() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.getTrainerByUsername("username"));

        verify(trainerRepository, times(1)).findByUserUsername("username");
    }

    @Test
    @DisplayName("Should return Trainee when changePassword")
    void shouldReturnTrainerWhenChangePassword() {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("0123456789")
                .newPassword("newPassword")
                .build();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer result = trainerService.changePassword(credentialsUpdateDTO);

        verify(trainerRepository, times(1)).save(trainer);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should throw IncorrectPasswordException for incorrect old password")
    void shouldThrowIncorrectPasswordExceptionWhenOldPasswordIsIncorrect() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO("wrongOldPassword", "newPassword");

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));

        assertThrows(IncorrectPasswordException.class, () -> trainerService.changePassword(credentialsUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IdenticalPasswordException when old password equals new password")
    void shouldThrowIdenticalPasswordExceptionWhenOldPasswordEqualsNewPassword() {
        CredentialsUpdateDTO credentialsUpdateDTO =
                createCredentialsUpdateDTO("0123456789", "0123456789");

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.ofNullable(trainer));

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

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        TrainerUpdateDTO trainerUpdateDTO = createTrainerUpdateDTO();

        trainerService.updateTrainer(trainerUpdateDTO);

        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    @DisplayName("Should throw TrainingTypeNotFoundException for incorrect TrainingType when updateTrainer")
    void shouldThrowTrainingTypeNotFoundExceptionForIncorrectTrainingTypeNameWhenUpdateTrainer() {
        TrainerUpdateDTO trainerUpdateDTO = createTrainerUpdateDTO();

        when(trainerRepository.findByUserUsername(any())).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainerService.updateTrainer(trainerUpdateDTO));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when toggleTrainerActivation")
    void shouldReturnTrueWhenToggleTrainerActivation() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        boolean result = trainerService.toggleTrainerActivation(trainer.getUsername(), trainer.getUser().isActive());

        verify(trainerRepository, times(1)).save(trainer);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException for invalid username in toggleTrainerActivation")
    void shouldThrowTrainerNotFoundExceptionForInvalidUsernameWhenToggleTrainerActivation() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerService.toggleTrainerActivation("Bad.Username", true));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when deleteTrainer")
    void shouldReturnTrueWhenDeleteTrainer() {
        String username = "testUser";
        when(trainerRepository.deleteByUserUsername(username)).thenReturn(true);

        boolean result = trainerService.deleteTrainer(username);

        verify(trainerRepository, times(1)).deleteByUserUsername(username);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return list of trainers when getNotAssignedTrainerList")
    void shouldReturnTrainerListWhenGetNotAssignedTrainerList() {
        String traineeUsername = "traineeUser";
        String password = "testPassword";
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername)).thenReturn(expectedTrainers);

        List<Trainer> result = trainerService.getNotAssignedTrainerList(traineeUsername);

        verify(trainerRepository, times(1)).findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername);
        assertEquals(expectedTrainers, result);
    }

    @Test
    @DisplayName("Should return list of trainers when getAllTrainers")
    void shouldReturnTrainerListWhenGetAllTrainers() {
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerRepository.findAll()).thenReturn(expectedTrainers);

        List<Trainer> result = trainerService.getAllTrainers();

        verify(trainerRepository, times(1)).findAll();
        assertEquals(expectedTrainers, result);
    }

    private CredentialsUpdateDTO createCredentialsUpdateDTO(String oldPassword,
                                                            String newPassword) {
        return CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }

    private TrainerUpdateDTO createTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization().getTrainingTypeName())
                .isActive(trainer.getUser().isActive())
                .build();
    }
}
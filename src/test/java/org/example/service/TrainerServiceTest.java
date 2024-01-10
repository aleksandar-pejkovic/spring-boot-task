package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.utils.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
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
    private TraineeDAO traineeDAO;

    @MockBean
    private TrainerDAO trainerDAO;

    @MockBean
    private TrainingDAO trainingDAO;

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

        when(trainingDAO.findTrainingTypeByName(any())).thenReturn(trainingType);
        when(credentialsGenerator.generateUsername(any())).thenReturn("Valentino.Rossi");
        when(credentialsGenerator.generateRandomPassword()).thenReturn("9876543210");
        when(trainerDAO.saveTrainer(any())).thenReturn(trainer);

        // Act
        Trainer result = trainerService.createTrainer(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getSpecialization().getTrainingTypeName());

        // Assert
        verify(trainerDAO, times(1)).saveTrainer(any());
    }

    @Test
    void getTrainerByUsername() {
        // Arrange
        String username = "testUser";
        Trainer expectedTrainer = new Trainer();
        when(trainerDAO.findTrainerByUsername(username)).thenReturn(expectedTrainer);

        // Act
        Trainer result = trainerService.getTrainerByUsername(username);

        // Assert
        verify(trainerDAO, times(1)).findTrainerByUsername(username);
        assertEquals(expectedTrainer, result);
    }

    @Test
    void changePassword() {
        // Arrange
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username("testUser")
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        when(trainerService.getTrainerByUsername(credentialsUpdateDTO.getUsername())).thenReturn(trainer);
        when(trainerDAO.updateTrainer(trainer)).thenReturn(trainer);

        // Act
        Trainer result = trainerService.changePassword(credentialsUpdateDTO);

        // Assert
        verify(trainerDAO, times(1)).updateTrainer(trainer);
        assertEquals(credentialsUpdateDTO.getNewPassword(), result.getPassword());
    }

    @Test
    void updateTrainer() {
        // Arrange
        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .build();

        when(trainerDAO.findTrainerByUsername(any())).thenReturn(trainer);
        when(trainingDAO.findTrainingTypeByName(any())).thenReturn(trainingType);
        when(trainerDAO.updateTrainer(trainer)).thenReturn(trainer);
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
        verify(trainerDAO, times(1)).updateTrainer(trainer);
    }

    @Test
    void toggleTrainerActivationTest() {
        // Arrange
        when(trainerDAO.findTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainerDAO.updateTrainer(trainer)).thenReturn(trainer);

        // Act
        boolean result = trainerService.toggleTrainerActivation(trainer.getUsername(), trainer.getUser().isActive());

        // Assert
        verify(trainerDAO, times(1)).updateTrainer(trainer);
        assertTrue(result);
    }

    @Test
    void deleteTrainer() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        when(trainerDAO.deleteTrainerByUsername(username)).thenReturn(true);

        // Act
        boolean result = trainerService.deleteTrainer(username, password);

        // Assert
        verify(trainerDAO, times(1)).deleteTrainerByUsername(username);
        assertTrue(result);
    }

    @Test
    void getNotAssignedTrainerList() {
        // Arrange
        String traineeUsername = "traineeUser";
        String password = "testPassword";
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerDAO.getNotAssignedTrainers(traineeUsername)).thenReturn(expectedTrainers);

        // Act
        List<Trainer> result = trainerService.getNotAssignedTrainerList(traineeUsername);

        // Assert
        verify(trainerDAO, times(1)).getNotAssignedTrainers(traineeUsername);
        assertEquals(expectedTrainers, result);
    }

    @Test
    void getAllTrainers() {
        // Arrange
        List<Trainer> expectedTrainers = Collections.singletonList(new Trainer());
        when(trainerDAO.getAllTrainers()).thenReturn(expectedTrainers);

        // Act
        List<Trainer> result = trainerService.getAllTrainers();

        // Assert
        verify(trainerDAO, times(1)).getAllTrainers();
        assertEquals(expectedTrainers, result);
    }

}
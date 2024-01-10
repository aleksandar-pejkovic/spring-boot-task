package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dto.training.TrainingCreateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrainingService.class})
class TrainingServiceTest {

    @MockBean
    private TrainingDAO trainingDAO;

    @MockBean
    private TraineeDAO traineeDAO;

    @MockBean
    private TrainerDAO trainerDAO;

    @Autowired
    private TrainingService trainingService;

    private Training training;

    private Trainee trainee;

    private Trainer trainer;

    @BeforeEach
    void setUp() throws Exception {
        User user1 = User.builder()
                .isActive(true)
                .lastName("Biaggi")
                .firstName("Max")
                .username("Max.Biaggi")
                .password("0123456789")
                .build();

        User user2 = User.builder()
                .isActive(true)
                .lastName("Storrari")
                .firstName("Matteo")
                .username("Matteo.Storrari")
                .password("0123456789")
                .build();

        trainee = Trainee.builder()
                .user(user1)
                .address("11000 Belgrade")
                .dateOfBirth(new Date())
                .trainerList(new ArrayList<>())
                .trainingList(new ArrayList<>())
                .build();

        trainer = Trainer.builder()
                .user(user2)
                .traineeList(new ArrayList<>())
                .trainingList(new ArrayList<>())
                .build();

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(
                TrainingType.builder()
                        .id(1L)
                        .trainingTypeName(TrainingTypeName.AEROBIC)
                        .build()
        );
    }

    @Test
    void createTraining() {
        // Arrange
        TrainingCreateDTO trainingCreateDTO = TrainingCreateDTO.builder()
                .traineeUsername(training.getTrainee().getUsername())
                .trainerUsername(training.getTrainer().getUsername())
                .trainingTypeName(training.getTrainingType().getTrainingTypeName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();

        when(traineeDAO.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(trainerDAO.findTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainingDAO.findTrainingTypeByName(any())).thenReturn(training.getTrainingType());
        when(trainingDAO.saveTraining(any())).thenReturn(training);

        // Act
        boolean result = trainingService.createTraining(trainingCreateDTO);

        // Assert
        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDAO, times(1)).saveTraining(trainingCaptor.capture());
        assertTrue(result);
        assertEquals(training.getTrainingDuration(), trainingCaptor.getValue().getTrainingDuration());
    }

    @Test
    void getTrainingById() {
        // Arrange
        when(trainingDAO.findById(1L)).thenReturn(training);

        // Act
        Training result = trainingService.getTrainingById(1L);

        // Assert
        verify(trainingDAO, times(1)).findById(1L);
        assertEquals(training, result);
    }

    @Test
    void updateTraining() {
        // Arrange
        when(trainingDAO.updateTraining(training)).thenReturn(training);

        // Act
        Training result = trainingService.updateTraining(training);

        // Assert
        verify(trainingDAO, times(1)).updateTraining(training);
        assertEquals(training, result);
    }

    @Test
    void deleteTraining() {
        // Arrange
        when(trainingDAO.deleteTraining(training)).thenReturn(true);

        // Act
        boolean result = trainingService.deleteTraining(training);

        // Assert
        verify(trainingDAO, times(1)).deleteTraining(training);
        assertTrue(result);
    }

    @Test
    void getTraineeTrainingList() {
        // Arrange
        int trainingDuration = 10;
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingDAO.getTraineeTrainingList(anyString(), any(), any(), anyString(), anyString())).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getTraineeTrainingList(
                trainee.getUsername(),
                new Date(),
                new Date(),
                training.getTrainer().getUsername(),
                training.getTrainingType().getTrainingTypeName().name()
        );

        // Assert
        verify(trainingDAO, times(1)).getTraineeTrainingList(anyString(), any(), any(), anyString(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    void getTrainerTrainingList() {
        // Arrange
        int trainingDuration = 10;
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingDAO.getTrainerTrainingList(anyString(), any(), any(), anyString())).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getTrainerTrainingList(
                trainer.getUsername(),
                new Date(),
                new Date(),
                training.getTrainee().getUsername()
        );

        // Assert
        verify(trainingDAO, times(1)).getTrainerTrainingList(anyString(), any(), any(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    void getAllTrainings() {
        // Arrange
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingDAO.findAllTrainings()).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getAllTrainings();

        // Assert
        verify(trainingDAO, times(1)).findAllTrainings();
        assertEquals(expectedTrainingList, result);
    }

}
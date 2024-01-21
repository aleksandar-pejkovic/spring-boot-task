package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dto.training.TrainingCreateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
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
    private TrainingRepository trainingRepository;

    @MockBean
    private TraineeRepository traineeRepository;

    @MockBean
    private TrainerRepository trainerRepository;

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

        when(traineeRepository.findTraineeByUsername(anyString())).thenReturn(trainee);
        when(trainerRepository.findTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainingRepository.findTrainingTypeByName(any())).thenReturn(training.getTrainingType());
        when(trainingRepository.save(any())).thenReturn(training);

        // Act
        boolean result = trainingService.createTraining(trainingCreateDTO);

        // Assert
        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository, times(1)).save(trainingCaptor.capture());
        assertTrue(result);
        assertEquals(training.getTrainingDuration(), trainingCaptor.getValue().getTrainingDuration());
    }

    @Test
    void getTrainingById() {
        // Arrange
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        // Act
        Training result = trainingService.getTrainingById(1L);

        // Assert
        verify(trainingRepository, times(1)).findById(1L);
        assertEquals(training, result);
    }

    @Test
    void updateTraining() {
        // Arrange
        when(trainingRepository.save(training)).thenReturn(training);

        // Act
        Training result = trainingService.updateTraining(training);

        // Assert
        verify(trainingRepository, times(1)).save(training);
        assertEquals(training, result);
    }

    @Test
    void deleteTraining() {
        // Arrange
        doNothing().when(trainingRepository).delete(training);

        // Act
        boolean result = trainingService.deleteTraining(training);

        // Assert
        verify(trainingRepository, times(1)).delete(training);
        assertTrue(result);
    }

    @Test
    void getTraineeTrainingList() {
        // Arrange
        int trainingDuration = 10;
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingRepository.getTraineeTrainingList(anyString(), any(), any(), anyString(), anyString())).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getTraineeTrainingList(
                trainee.getUsername(),
                new Date(),
                new Date(),
                training.getTrainer().getUsername(),
                training.getTrainingType().getTrainingTypeName().name()
        );

        // Assert
        verify(trainingRepository, times(1)).getTraineeTrainingList(anyString(), any(), any(), anyString(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    void getTrainerTrainingList() {
        // Arrange
        int trainingDuration = 10;
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingRepository.getTrainerTrainingList(anyString(), any(), any(), anyString())).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getTrainerTrainingList(
                trainer.getUsername(),
                new Date(),
                new Date(),
                training.getTrainee().getUsername()
        );

        // Assert
        verify(trainingRepository, times(1)).getTrainerTrainingList(anyString(), any(), any(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    void getAllTrainings() {
        // Arrange
        List<Training> expectedTrainingList = Collections.singletonList(training);
        when(trainingRepository.findAll()).thenReturn(expectedTrainingList);

        // Act
        List<Training> result = trainingService.getAllTrainings();

        // Assert
        verify(trainingRepository, times(1)).findAll();
        assertEquals(expectedTrainingList, result);
    }

}
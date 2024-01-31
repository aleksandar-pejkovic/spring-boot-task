package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dto.training.TrainingCreateDTO;
import org.example.exception.notfound.TraineeNotFoundException;
import org.example.exception.notfound.TrainerNotFoundException;
import org.example.exception.notfound.TrainingTypeNotFoundException;
import org.example.model.Training;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.utils.dummydata.TrainingDummyDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private TrainingTypeRepository trainingTypeRepository;

    @MockBean
    private TraineeRepository traineeRepository;

    @MockBean
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingService trainingService;

    private Training trainingUnderTest;

    @BeforeEach
    void setUp() {
        trainingUnderTest = TrainingDummyDataFactory.getTrainingUnderTest();
    }

    @Test
    @DisplayName("Should return true when createTraining")
    void shouldReturnTrueWhenCreateTraining() {
        TrainingCreateDTO trainingCreateDTO = createTrainingCreateDTO();

        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainingUnderTest.getTrainee()));
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainingUnderTest.getTrainer()));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.of(trainingUnderTest.getTrainingType()));
        when(trainingRepository.save(any())).thenReturn(trainingUnderTest);

        boolean result = trainingService.createTraining(trainingCreateDTO);

        ArgumentCaptor<Training> trainingCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(trainingCaptor.capture());
        assertTrue(result);
        assertEquals(trainingUnderTest.getTrainingDuration(), trainingCaptor.getValue().getTrainingDuration());
    }


    @Test
    @DisplayName("Should throw TraineeNotFoundException for invalid traineeUsername when createTraining")
    void shouldThrowTraineeNotFoundExceptionForInvalidTraineeUsernameWhenCreateTraining() {
        TrainingCreateDTO trainingCreateDTO = createTrainingCreateDTO();
        when(traineeRepository.findByUserUsername(any())).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> trainingService.createTraining(trainingCreateDTO));

        verify(traineeRepository).findByUserUsername(trainingCreateDTO.getTraineeUsername());
    }

    @Test
    @DisplayName("Should throw TrainerNotFoundException for invalid trainerUsername when createTraining")
    void shouldThrowTrainerNotFoundExceptionForInvalidTrainerUsernameWhenCreateTraining() {
        TrainingCreateDTO trainingCreateDTO = createTrainingCreateDTO();
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainingUnderTest.getTrainee()));
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class, () -> trainingService.createTraining(trainingCreateDTO));

        verify(trainerRepository).findByUserUsername(trainingCreateDTO.getTrainerUsername());
    }

    @Test
    @DisplayName("Should throw TrainingTypeNotFound for invalid trainingTypeName when createTraining")
    void shouldThrowTrainingTypeNotFoundExceptionForInvalidTrainingTypeNameWhenCreateTraining() {
        TrainingCreateDTO trainingCreateDTO = createTrainingCreateDTO();
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainingUnderTest.getTrainee()));
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainingUnderTest.getTrainer()));
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () -> trainingService.createTraining(trainingCreateDTO));

        verify(trainingTypeRepository).findByTrainingTypeName(trainingCreateDTO.getTrainingTypeName());
    }

    @Test
    @DisplayName("Should return Training when getTrainingById")
    void shouldReturnTrainingWhenGetTrainingById() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(trainingUnderTest));

        Training result = trainingService.getTrainingById(1L);

        verify(trainingRepository).findById(1L);
        assertEquals(trainingUnderTest, result);
    }

    @Test
    @DisplayName("Should return Training when updateTraining")
    void shouldReturnTrainingWhenUpdateTraining() {
        when(trainingRepository.save(trainingUnderTest)).thenReturn(trainingUnderTest);

        Training result = trainingService.updateTraining(trainingUnderTest);

        verify(trainingRepository).save(trainingUnderTest);
        assertEquals(trainingUnderTest, result);
    }

    @Test
    @DisplayName("Should return true when deleteTraining")
    void shouldReturnTrueWhenDeleteTraining() {
        doNothing().when(trainingRepository).delete(trainingUnderTest);

        boolean result = trainingService.deleteTraining(trainingUnderTest);

        verify(trainingRepository).delete(trainingUnderTest);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return list of trainings when getTraineeTrainingList")
    void shouldReturnTrainingListWhenGetTraineeTrainingList() {
        List<Training> expectedTrainingList = Collections.singletonList(trainingUnderTest);
        when(trainingRepository.findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameAndTrainingTypeTrainingTypeName(anyString(), any(), any(), anyString(), anyString())).thenReturn(expectedTrainingList);

        List<Training> result = trainingService.getTraineeTrainingList(
                trainingUnderTest.getTrainee().getUsername(),
                new Date(),
                new Date(),
                trainingUnderTest.getTrainer().getUsername(),
                trainingUnderTest.getTrainingType().getTrainingTypeName().name()
        );

        verify(trainingRepository).findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameAndTrainingTypeTrainingTypeName(anyString(), any(), any(), anyString(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    @DisplayName("Should return list of trainings when getTrainerTrainingList")
    void shouldReturnTrainingListWhenGetTrainerTrainingList() {
        List<Training> expectedTrainingList = Collections.singletonList(trainingUnderTest);
        when(trainingRepository.findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsername(anyString(), any(), any(), anyString())).thenReturn(expectedTrainingList);

        List<Training> result = trainingService.getTrainerTrainingList(
                trainingUnderTest.getTrainer().getUsername(),
                new Date(),
                new Date(),
                trainingUnderTest.getTrainee().getUsername()
        );

        verify(trainingRepository).findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsername(anyString(), any(), any(), anyString());
        assertEquals(expectedTrainingList, result);
    }

    @Test
    @DisplayName("Should return list of trainings when getAllTrainings")
    void shouldReturnTrainingListWhenGetAllTrainings() {
        List<Training> expectedTrainingList = Collections.singletonList(trainingUnderTest);
        when(trainingRepository.findAll()).thenReturn(expectedTrainingList);

        List<Training> result = trainingService.getAllTrainings();

        verify(trainingRepository).findAll();
        assertEquals(expectedTrainingList, result);
    }

    private TrainingCreateDTO createTrainingCreateDTO() {
        return TrainingCreateDTO.builder()
                .traineeUsername(trainingUnderTest.getTrainee().getUsername())
                .trainerUsername(trainingUnderTest.getTrainer().getUsername())
                .trainingTypeName(trainingUnderTest.getTrainingType().getTrainingTypeName())
                .trainingDate(trainingUnderTest.getTrainingDate())
                .trainingDuration(trainingUnderTest.getTrainingDuration())
                .build();
    }
}
package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.dto.training.TrainingCreateDTO;
import org.example.dto.training.TrainingDTO;
import org.example.dto.trainingType.TrainingTypeDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrainingController.class})
class TrainingControllerTest {

    @Autowired
    private TrainingController trainingController;

    @MockBean
    private TrainingService trainingService;

    @Test
    void getTraineeTrainingsList() {
        String username = "Trainee1";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String trainerName = "Trainer1";
        TrainingTypeName trainingType = TrainingTypeName.AEROBIC;

        List<Training> trainings = new ArrayList<>();
        when(trainingService.getTraineeTrainingList(username, periodFrom, periodTo, trainerName, trainingType.name()))
                .thenReturn(trainings);

        List<TrainingDTO> result = trainingController.getTraineeTrainingsList(
                username, periodFrom, periodTo, trainerName, trainingType);

        verify(trainingService, times(1)).getTraineeTrainingList(
                username, periodFrom, periodTo, trainerName, trainingType.name());
        assertEquals(0, result.size());
    }

    @Test
    void getTrainerTrainingsList() {
        String username = "Trainer1";
        Date periodFrom = new Date();
        Date periodTo = new Date();
        String traineeName = "Trainee1";

        List<Training> trainings = new ArrayList<>();
        when(trainingService.getTrainerTrainingList(username, periodFrom, periodTo, traineeName))
                .thenReturn(trainings);

        List<TrainingDTO> result = trainingController.getTrainerTrainingsList(username, periodFrom, periodTo, traineeName);

        verify(trainingService, times(1)).getTrainerTrainingList(username, periodFrom, periodTo, traineeName);
        assertEquals(0, result.size());
    }

    @Test
    void addTraining() {
        TrainingCreateDTO trainingCreateDTO = TrainingCreateDTO.builder().build();

        when(trainingService.createTraining(any())).thenReturn(true);

        ResponseEntity<Boolean> result = trainingController.addTraining(trainingCreateDTO);

        verify(trainingService, times(1)).createTraining(trainingCreateDTO);
        assertEquals(true, result.getBody());
    }

    @Test
    void getAllTrainingTypes() {
        List<TrainingTypeDTO> trainingTypes = new ArrayList<>();
        when(trainingService.finaAllTrainingTypes()).thenReturn(trainingTypes);

        List<TrainingTypeDTO> result = trainingController.getAllTrainingTypes();

        verify(trainingService, times(1)).finaAllTrainingTypes();
        assertEquals(0, result.size());
    }
}

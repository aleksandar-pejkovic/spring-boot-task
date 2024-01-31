package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.example.dto.training.TrainingCreateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.service.TrainingService;
import org.example.utils.dummydata.TrainingDummyDataFactory;
import org.example.utils.dummydata.TrainingTypeDummyDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TrainingControllerTest {

    private static final String URL_TEMPLATE = "/api/trainings";
    private static final String URL_TRAINEE = "/trainee";
    private static final String URL_TRAINER = "/trainer";
    private static final String URL_TRAINING_TYPES = "/training-types";

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_TRAINER_NAME = "trainerName";
    private static final String PARAM_TRAINING_TYPE = "trainingType";

    private static final String TRAINEE_USERNAME = "John.Doe";
    private static final String TRAINER_USERNAME = "Joe.Johnson";
    private static final Date TRAINING_DATE = new Date();
    private static final int TRAINING_DURATION = 30;

    private final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @Autowired
    public TrainingControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    @WithMockUser
    void getTraineeTrainingsList() throws Exception {
        List<Training> trainings = TrainingDummyDataFactory.getTrainingsForTrainee();

        when(trainingService.getTraineeTrainingList(any(), any(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get(URL_TEMPLATE + URL_TRAINEE)
                        .param(PARAM_USERNAME, TRAINEE_USERNAME)
                        .param(PARAM_TRAINER_NAME, TRAINER_USERNAME)
                        .param(PARAM_TRAINING_TYPE, TrainingTypeName.AEROBIC.name()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTrainerTrainingsList() throws Exception {
        List<Training> trainings = TrainingDummyDataFactory.getTrainingsForTrainer();

        when(trainingService.getTrainerTrainingList(any(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get(URL_TEMPLATE + URL_TRAINER)
                        .param(PARAM_USERNAME, TRAINER_USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addTraining() throws Exception {
        TrainingCreateDTO trainingCreateDTO = TrainingCreateDTO.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();

        when(trainingService.createTraining(any())).thenReturn(true);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingCreateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addTrainingReturnsBadRequestWhenCreateTrainingUnsuccessful() throws Exception {
        TrainingCreateDTO trainingCreateDTO = TrainingCreateDTO.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();

        when(trainingService.createTraining(any())).thenReturn(false);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllTrainingTypes() throws Exception {
        List<TrainingType> trainingTypes = TrainingTypeDummyDataFactory.getTrainingTypes();
        when(trainingService.finaAllTrainingTypes()).thenReturn(trainingTypes);

        mockMvc.perform(get(URL_TEMPLATE + URL_TRAINING_TYPES))
                .andExpect(status().isOk());
    }
}

package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.dto.trainingType.TrainingTypeDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

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
    private static final String PARAM_TRAINEE_USERNAME = "traineeUsername";
    private static final String PARAM_TRAINER_USERNAME = "trainerUsername";
    private static final String PARAM_TRAINING_TYPE_NAME = "trainingTypeName";
    private static final String PARAM_TRAINING_DATE = "trainingDate";
    private static final String PARAM_TRAINING_DURATION = "trainingDuration";

    private static final String TRAINEE_USERNAME = "John.Doe";
    private static final String TRAINER_USERNAME = "Joe.Johnson";
    private static final String TRAINING_DATE = "2024-01-07";
    private static final int TRAINING_DURATION = 30;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @Test
    @WithMockUser
    void getTraineeTrainingsList() throws Exception {
        List<Training> trainings = new ArrayList<>();

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
        List<Training> trainings = new ArrayList<>();
        when(trainingService.getTrainerTrainingList(any(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get(URL_TEMPLATE + URL_TRAINER)
                        .param(PARAM_USERNAME, TRAINER_USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addTraining() throws Exception {
        String trainingCreateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put(PARAM_TRAINEE_USERNAME, TRAINEE_USERNAME);
            put(PARAM_TRAINER_USERNAME, TRAINER_USERNAME);
            put(PARAM_TRAINING_TYPE_NAME, TrainingTypeName.AEROBIC.name());
            put(PARAM_TRAINING_DATE, TRAINING_DATE);
            put(PARAM_TRAINING_DURATION, TRAINING_DURATION);
        }}).jsonString();

        when(trainingService.createTraining(any())).thenReturn(true);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingCreateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addTrainingReturnsBadRequestWhenCreateTrainingUnsuccessful() throws Exception {
        String trainingCreateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put(PARAM_TRAINEE_USERNAME, TRAINEE_USERNAME);
            put(PARAM_TRAINER_USERNAME, TRAINER_USERNAME);
            put(PARAM_TRAINING_TYPE_NAME, TrainingTypeName.AEROBIC.name());
            put(PARAM_TRAINING_DATE, TRAINING_DATE);
            put(PARAM_TRAINING_DURATION, TRAINING_DURATION);
        }}).jsonString();

        when(trainingService.createTraining(any())).thenReturn(false);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainingCreateDTOJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllTrainingTypes() throws Exception {
        List<TrainingTypeDTO> trainingTypes = new ArrayList<>();
        when(trainingService.finaAllTrainingTypes()).thenReturn(trainingTypes);

        mockMvc.perform(get(URL_TEMPLATE + URL_TRAINING_TYPES))
                .andExpect(status().isOk());
    }
}

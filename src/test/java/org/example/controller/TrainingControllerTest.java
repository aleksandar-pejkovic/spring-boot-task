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

        mockMvc.perform(get(URL_TEMPLATE + "/trainee")
                        .param("username", "John.Doe")
                        .param("trainerName", "Max.Biaggi")
                        .param("trainingType", "AEROBIC"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTrainerTrainingsList() throws Exception {
        List<Training> trainings = new ArrayList<>();
        when(trainingService.getTrainerTrainingList(any(), any(), any(), any()))
                .thenReturn(trainings);

        mockMvc.perform(get(URL_TEMPLATE + "/trainer")
                        .param("username", "John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addTraining() throws Exception {
        String trainingCreateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("traineeUsername", "John.Doe");
            put("trainerUsername", "Max.Biaggi");
            put("trainingTypeName", "AEROBIC");
            put("trainingDate", "2024-01-07");
            put("trainingDuration", 30);
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
            put("traineeUsername", "John.Doe");
            put("trainerUsername", "Max.Biaggi");
            put("trainingTypeName", "AEROBIC");
            put("trainingDate", "2024-01-07");
            put("trainingDuration", 30);
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

        mockMvc.perform(get(URL_TEMPLATE + "/training-types"))
                .andExpect(status().isOk());
    }
}

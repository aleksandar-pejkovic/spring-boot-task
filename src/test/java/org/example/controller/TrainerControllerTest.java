package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
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
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .isActive(true)
                .lastName("Doe")
                .firstName("John")
                .username("John.Doe")
                .password("0123456789")
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName(TrainingTypeName.AEROBIC)
                .trainerList(new ArrayList<>())
                .build();

        trainer = Trainer.builder()
                .specialization(trainingType)
                .user(user)
                .traineeList(new ArrayList<>())
                .trainingList(new ArrayList<>())
                .build();
    }

    @Test
    void traineeRegistration() throws Exception {
        TrainingTypeName specialization = TrainingTypeName.AEROBIC;

        when(trainerService.createTrainer(anyString(), anyString(), any())).thenReturn(trainer);

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("specialization", TrainingTypeName.AEROBIC.name()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void changeLogin() throws Exception {

        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("oldPassword", "1234567890");
            put("newPassword", "0123456789");
        }}).jsonString();

        when(trainerService.changePassword(any())).thenReturn(trainer);

        mockMvc.perform(put("/api/trainers/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(any())).thenReturn(trainer);

        mockMvc.perform(get("/api/trainers/John.Doe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateTrainerProfile() throws Exception {
        when(trainerService.updateTrainer(any())).thenReturn(trainer);

        String trainerUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("firstName", "John");
            put("lastName", "Doe");
            put("specialization", "AEROBIC");
            put("isActive", "true");
        }}).jsonString();

        mockMvc.perform(put("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getNotAssignedOnTrainee() throws Exception {
        List<Trainer> unassignedTrainers = new ArrayList<>();
        when(trainerService.getNotAssignedTrainerList(any())).thenReturn(unassignedTrainers);

        mockMvc.perform(get("/api/trainers/unassigned")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("traineeUsername", "John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateTraineeTrainerList() throws Exception {
        List<Trainer> updatedTraineeTrainerList = new ArrayList<>();
        when(trainerService.updateTraineeTrainerList(any(), any()))
                .thenReturn(updatedTraineeTrainerList);

        mockMvc.perform(put("/api/trainers/{traineeUsername}/updateTrainers", "John.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsernameList\":[\"Trainer1\", \"Trainer2\", \"Trainer3\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivation() throws Exception {
        String username = "John.Doe";
        boolean isActive = true;
        when(trainerService.toggleTrainerActivation(username, isActive)).thenReturn(true);

        mockMvc.perform(patch("/api/trainers")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }
}

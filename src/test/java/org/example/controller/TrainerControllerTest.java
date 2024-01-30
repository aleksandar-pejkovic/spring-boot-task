package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.enums.TrainingTypeName;
import org.example.exception.notfound.TrainerNotFoundException;
import org.example.model.Trainer;
import org.example.service.TrainerService;
import org.example.utils.dummydata.TrainerDummyDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TrainerControllerTest {

    private static final String URL_TEMPLATE = "/api/trainers";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private Trainer trainerUnderTest;

    @BeforeEach
    void setUp() {
        trainerUnderTest = TrainerDummyDataFactory.getTrainerUnderTestingJoeJohnson();
    }

    @Test
    void traineeRegistration() throws Exception {
        when(trainerService.createTrainer(anyString(), anyString(), any())).thenReturn(trainerUnderTest);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "Joe")
                        .param("lastName", "Johnson")
                        .param("specialization", TrainingTypeName.AEROBIC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Joe.Johnson"));
    }

    @Test
    @WithMockUser
    void changeLogin() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "Joe.Johnson");
            put("oldPassword", "1234567890");
            put("newPassword", "0123456789");
        }}).jsonString();

        when(trainerService.changePassword(any())).thenReturn(trainerUnderTest);

        mockMvc.perform(put(URL_TEMPLATE + "/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void changeLoginReturnBadRequestWhenTrainerIsNull() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "Joe.Johnson");
            put("oldPassword", "1234567890");
            put("newPassword", "0123456789");
        }}).jsonString();

        when(trainerService.changePassword(any())).thenReturn(null);

        mockMvc.perform(put(URL_TEMPLATE + "/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(any())).thenReturn(trainerUnderTest);

        mockMvc.perform(get(URL_TEMPLATE + "/Joe.Johnson")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Joe.Johnson"))
                .andExpect(jsonPath("$.firstName").value("Joe"))
                .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForBadUsernameWhenGetTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(anyString())).thenThrow(new TrainerNotFoundException("Trainer not " +
                "found"));

        mockMvc.perform(get(URL_TEMPLATE + "/John.Doe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTrainerProfile() throws Exception {
        when(trainerService.updateTrainer(any())).thenReturn(trainerUnderTest);

        String trainerUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "Joe.Johnson");
            put("firstName", "Joe");
            put("lastName", "Johnson");
            put("specialization", "AEROBIC");
            put("isActive", "true");
        }}).jsonString();

        mockMvc.perform(put(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trainerUpdateDTOJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("Joe.Johnson"))
                .andExpect(jsonPath("$.firstName").value("Joe"))
                .andExpect(jsonPath("$.lastName").value("Johnson"));
    }

    @Test
    @WithMockUser
    void deleteTrainerProfile() throws Exception {
        when(trainerService.deleteTrainer(anyString())).thenReturn(true);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "Joe.Johnson"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTrainerProfileReturnsBadRequestWhenDeletionUnsuccessful() throws Exception {
        when(trainerService.deleteTrainer(anyString())).thenReturn(false);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "Joe.Johnson"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getNotAssignedOnTrainee() throws Exception {
        List<Trainer> unassignedTrainers = new ArrayList<>();
        when(trainerService.getNotAssignedTrainerList(any())).thenReturn(unassignedTrainers);

        mockMvc.perform(get(URL_TEMPLATE + "/unassigned")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("traineeUsername", "Joe.Johnson"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateTraineeTrainerList() throws Exception {
        List<Trainer> updatedTraineeTrainerList = new ArrayList<>();
        when(trainerService.updateTraineeTrainerList(any(), any()))
                .thenReturn(updatedTraineeTrainerList);

        mockMvc.perform(put(URL_TEMPLATE + "/{traineeUsername}/updateTrainers", "Joe.Johnson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsernameList\":[\"Joe.Johnson\", \"Peter.Peterson\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivation() throws Exception {
        String username = "Joe.Johnson";
        boolean isActive = true;
        when(trainerService.toggleTrainerActivation(username, isActive)).thenReturn(true);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "Joe.Johnson")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTrainerActivationReturnsBadRequestWhenToggleUnsuccessful() throws Exception {
        when(trainerService.toggleTrainerActivation(anyString(), anyBoolean())).thenReturn(false);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "Joe.Johnson")
                        .param("isActive", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isUnauthorized());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }

    @Test
    @WithMockUser(username = "John.Doe", authorities = {"ROLE_TEST"})
    void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().is3xxRedirection());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnAuthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isUnauthorized());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }
}

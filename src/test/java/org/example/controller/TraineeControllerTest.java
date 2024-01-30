package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.example.model.Trainee;
import org.example.service.TraineeService;
import org.example.utils.dummydata.TraineeDummyDataFactory;
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
public class TraineeControllerTest {

    private static final String URL_TEMPLATE = "/api/trainees";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeService traineeService;

    private Trainee traineeUnderTest;

    @BeforeEach
    void setUp() {
        traineeUnderTest = TraineeDummyDataFactory.getTraineeUnderTestJohnDoe();
    }

    @Test
    void traineeRegistration() throws Exception {
        when(traineeService.createTrainee(any(), any(), any(), any())).thenReturn(traineeUnderTest);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John.Doe"));
    }

    @Test
    @WithMockUser
    void changeLogin() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("oldPassword", "1234567890");
            put("newPassword", "0123456789");
        }}).jsonString();

        when(traineeService.changePassword(any())).thenReturn(traineeUnderTest);

        mockMvc.perform(put(URL_TEMPLATE + "/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void changeLoginReturnBadRequestWhenTraineeIsNull() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("oldPassword", "1234567890");
            put("newPassword", "0123456789");
        }}).jsonString();

        when(traineeService.changePassword(any())).thenReturn(null);

        mockMvc.perform(put(URL_TEMPLATE + "/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTraineeByUsername() throws Exception {
        when(traineeService.getTraineeByUsername(anyString())).thenReturn(traineeUnderTest);

        mockMvc.perform(get(URL_TEMPLATE + "/John.Doe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser
    void updateTraineeProfile() throws Exception {
        String traineeUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("firstName", "John");
            put("lastName", "Doe");
        }}).jsonString();

        when(traineeService.updateTrainee(any())).thenReturn(traineeUnderTest);

        mockMvc.perform(put(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traineeUpdateDTOJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser
    void deleteTraineeProfile() throws Exception {
        when(traineeService.deleteTrainee(anyString())).thenReturn(true);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTraineeProfileReturnsBadRequestWhenDeletionUnsuccessful() throws Exception {
        when(traineeService.deleteTrainee(anyString())).thenReturn(false);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivation() throws Exception {
        when(traineeService.toggleTraineeActivation(anyString(), anyBoolean())).thenReturn(true);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivationReturnsBadRequestWhenToggleUnsuccessful() throws Exception {
        when(traineeService.toggleTraineeActivation(anyString(), anyBoolean())).thenReturn(false);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isBadRequest());
    }
}

package org.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.example.model.Trainee;
import org.example.model.User;
import org.example.service.TraineeService;
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

    private Trainee trainee;

    @BeforeEach
    void setUp() throws Exception {
        User user = User.builder()
                .isActive(true)
                .lastName("Doe")
                .firstName("John")
                .username("John.Doe")
                .password("0123456789")
                .build();

        trainee = Trainee.builder()
                .user(user)
                .address("11000 Belgrade")
                .dateOfBirth(new Date())
                .trainerList(new ArrayList<>())
                .build();
    }

    @Test
    void traineeRegistration() throws Exception {
        when(traineeService.createTrainee(any(), any(), any(), any())).thenReturn(trainee);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
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

        when(traineeService.changePassword(any())).thenReturn(trainee);

        mockMvc.perform(put(URL_TEMPLATE + "/change-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTraineeByUsername() throws Exception {
        when(traineeService.getTraineeByUsername(anyString())).thenReturn(trainee);

        mockMvc.perform(get(URL_TEMPLATE + "/John.Doe")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateTraineeProfile() throws Exception {
        String traineeUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put("username", "John.Doe");
            put("firstName", "John");
            put("lastName", "Doe");
        }}).jsonString();

        when(traineeService.updateTrainee(any())).thenReturn(trainee);

        mockMvc.perform(put(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traineeUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTraineeProfile() throws Exception {
        String username = "John.Doe";
        when(traineeService.deleteTrainee(username)).thenReturn(true);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivation() throws Exception {
        String username = "John.Doe";
        boolean isActive = true;
        when(traineeService.toggleTraineeActivation(username, isActive)).thenReturn(true);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "John.Doe")
                        .param("isActive", "true"))
                .andExpect(status().isOk());
    }
}

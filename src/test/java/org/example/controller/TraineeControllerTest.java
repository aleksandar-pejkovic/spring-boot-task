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

import java.util.HashMap;

import org.example.exception.notfound.TraineeNotFoundException;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class TraineeControllerTest {

    private static final String URL_TEMPLATE = "/api/trainees";
    private static final String URL_CHANGE_LOGIN = "/change-login";
    private static final String URL_USERNAME = "/{username}";

    private static final String USERNAME = "John.Doe";
    private static final String PASSWORD = "0123456789";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String ACTIVE_STATUS = "true";

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_FIRST_NAME = "firstName";
    private static final String PARAM_LAST_NAME = "lastName";
    private static final String PARAM_OLD_PASSWORD = "oldPassword";
    private static final String PARAM_NEW_PASSWORD = "newPassword";
    private static final String PARAM_IS_ACTIVE = "isActive";

    private static final String JSON_PATH_USERNAME = "$.username";
    private static final String JSON_PATH_PASSWORD = "$.password";
    private static final String JSON_PATH_FIRST_NAME = "$.firstName";
    private static final String JSON_PATH_LAST_NAME = "$.lastName";

    private static final String ROLE_TEST = "ROLE_TEST";

    private static final String NOT_FOUND_MESSAGE_TRAINEE = "Trainee not found";

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
                        .param(PARAM_FIRST_NAME, FIRST_NAME)
                        .param(PARAM_LAST_NAME, LAST_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_USERNAME).value(USERNAME))
                .andExpect(jsonPath(JSON_PATH_PASSWORD).value(PASSWORD));
    }

    @Test
    @WithMockUser
    void changeLogin() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put(PARAM_USERNAME, USERNAME);
            put(PARAM_OLD_PASSWORD, PASSWORD);
            put(PARAM_NEW_PASSWORD, NEW_PASSWORD);
        }}).jsonString();

        when(traineeService.changePassword(any())).thenReturn(traineeUnderTest);

        mockMvc.perform(put(URL_TEMPLATE + URL_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void changeLoginReturnBadRequestWhenTraineeIsNull() throws Exception {
        String credentialsUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put(PARAM_USERNAME, USERNAME);
            put(PARAM_OLD_PASSWORD, PASSWORD);
            put(PARAM_NEW_PASSWORD, NEW_PASSWORD);
        }}).jsonString();

        when(traineeService.changePassword(any())).thenReturn(null);

        mockMvc.perform(put(URL_TEMPLATE + URL_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentialsUpdateDTOJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTraineeByUsername() throws Exception {
        when(traineeService.getTraineeByUsername(anyString())).thenReturn(traineeUnderTest);

        mockMvc.perform(get(URL_TEMPLATE + URL_USERNAME, USERNAME)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_USERNAME).value(USERNAME))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(FIRST_NAME))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(LAST_NAME));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForBadUsernameWhenGetTraineeByUsername() throws Exception {
        when(traineeService.getTraineeByUsername(anyString())).thenThrow(new TraineeNotFoundException(NOT_FOUND_MESSAGE_TRAINEE));

        mockMvc.perform(get(URL_TEMPLATE + URL_USERNAME, USERNAME)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTraineeProfile() throws Exception {
        String traineeUpdateDTOJson = JsonPath.parse(new HashMap<String, Object>() {{
            put(PARAM_USERNAME, USERNAME);
            put(PARAM_FIRST_NAME, FIRST_NAME);
            put(PARAM_LAST_NAME, LAST_NAME);
        }}).jsonString();

        when(traineeService.updateTrainee(any())).thenReturn(traineeUnderTest);

        mockMvc.perform(put(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(traineeUpdateDTOJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_USERNAME).value(USERNAME))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(FIRST_NAME))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(LAST_NAME));
    }

    @Test
    @WithMockUser
    void deleteTraineeProfile() throws Exception {
        when(traineeService.deleteTrainee(anyString())).thenReturn(true);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTraineeProfileReturnsBadRequestWhenDeletionUnsuccessful() throws Exception {
        when(traineeService.deleteTrainee(anyString())).thenReturn(false);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivation() throws Exception {
        when(traineeService.toggleTraineeActivation(anyString(), anyBoolean())).thenReturn(true);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTraineeActivationReturnsBadRequestWhenToggleUnsuccessful() throws Exception {
        when(traineeService.toggleTraineeActivation(anyString(), anyBoolean())).thenReturn(false);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isUnauthorized());

        verify(traineeService, never()).toggleTraineeActivation(anyString(), anyBoolean());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = {ROLE_TEST})
    void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().is3xxRedirection());

        verify(traineeService, never()).toggleTraineeActivation(anyString(), anyBoolean());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnAuthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isUnauthorized());

        verify(traineeService, never()).toggleTraineeActivation(anyString(), anyBoolean());
    }
}

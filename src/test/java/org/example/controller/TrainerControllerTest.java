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

import java.util.List;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class TrainerControllerTest {

    private static final String URL_TEMPLATE = "/api/trainers";
    private static final String URL_USERNAME = "/{username}";
    private static final String URL_TRAINEE_USERNAME = "/{traineeUsername}";
    private static final String URL_CHANGE_LOGIN = "/change-login";
    private static final String URL_UPDATE_TRAINERS = "/updateTrainers";
    private static final String URL_UNASSIGNED = "/unassigned";

    private static final String USERNAME = "Joe.Johnson";
    private static final String PASSWORD = "0123456789";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String FIRST_NAME = "Joe";
    private static final String LAST_NAME = "Johnson";
    private static final String ACTIVE_STATUS = "true";
    private static final String TRAINEE_USERNAME = "John.Doe";

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_TRAINEE_USERNAME = "traineeUsername";
    private static final String PARAM_FIRST_NAME = "firstName";
    private static final String PARAM_LAST_NAME = "lastName";
    private static final String PARAM_IS_ACTIVE = "isActive";
    private static final String PARAM_SPECIALIZATION = "specialization";

    private static final String JSON_PATH_USERNAME = "$.username";
    private static final String JSON_PATH_PASSWORD = "$.password";
    private static final String JSON_PATH_FIRST_NAME = "$.firstName";
    private static final String JSON_PATH_LAST_NAME = "$.lastName";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_TEST = "ROLE_TEST";

    private static final String NOT_FOUND_MESSAGE_TRAINER = "Trainer not found";

    private final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private Trainer trainerUnderTest;

    @Autowired
    public TrainerControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        trainerUnderTest = TrainerDummyDataFactory.getTrainerUnderTestingJoeJohnson();
    }

    @Test
    void traineeRegistration() throws Exception {
        when(trainerService.createTrainer(anyString(), anyString(), any())).thenReturn(trainerUnderTest);

        mockMvc.perform(post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_FIRST_NAME, FIRST_NAME)
                        .param(PARAM_LAST_NAME, LAST_NAME)
                        .param(PARAM_SPECIALIZATION, TrainingTypeName.AEROBIC.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_USERNAME).value(USERNAME))
                .andExpect(jsonPath(JSON_PATH_PASSWORD).value(PASSWORD));
    }

    @Test
    @WithMockUser
    void changeLogin() throws Exception {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();

        when(trainerService.changePassword(any())).thenReturn(trainerUnderTest);

        mockMvc.perform(put(URL_TEMPLATE + URL_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsUpdateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void changeLoginReturnBadRequestWhenTrainerIsNull() throws Exception {
        CredentialsUpdateDTO credentialsUpdateDTO = CredentialsUpdateDTO.builder()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();

        when(trainerService.changePassword(any())).thenReturn(null);

        mockMvc.perform(put(URL_TEMPLATE + URL_CHANGE_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(any())).thenReturn(trainerUnderTest);

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
    void shouldReturnNotFoundForBadUsernameWhenGetTrainerByUsername() throws Exception {
        when(trainerService.getTrainerByUsername(anyString())).thenThrow(new TrainerNotFoundException(NOT_FOUND_MESSAGE_TRAINER));

        mockMvc.perform(get(URL_TEMPLATE + URL_USERNAME, USERNAME)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTrainerProfile() throws Exception {
        TrainerUpdateDTO trainerUpdateDTO = TrainerUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(TrainingTypeName.AEROBIC)
                .isActive(true)
                .build();

        when(trainerService.updateTrainer(any())).thenReturn(trainerUnderTest);

        mockMvc.perform(put(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUpdateDTO)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_USERNAME).value(USERNAME))
                .andExpect(jsonPath(JSON_PATH_FIRST_NAME).value(FIRST_NAME))
                .andExpect(jsonPath(JSON_PATH_LAST_NAME).value(LAST_NAME));
    }

    @Test
    @WithMockUser
    void deleteTrainerProfile() throws Exception {
        when(trainerService.deleteTrainer(anyString())).thenReturn(true);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteTrainerProfileReturnsBadRequestWhenDeletionUnsuccessful() throws Exception {
        when(trainerService.deleteTrainer(anyString())).thenReturn(false);

        mockMvc.perform(delete(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getNotAssignedOnTrainee() throws Exception {
        List<Trainer> unassignedTrainers = TrainerDummyDataFactory.getUnassignedTrainers();
        when(trainerService.getNotAssignedTrainerList(any())).thenReturn(unassignedTrainers);

        mockMvc.perform(get(URL_TEMPLATE + URL_UNASSIGNED)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_TRAINEE_USERNAME, USERNAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateTraineeTrainerList() throws Exception {
        List<Trainer> updatedTraineeTrainerList = TrainerDummyDataFactory.getUpdatedTrainerListForTrainee();
        when(trainerService.updateTraineeTrainerList(any(), any()))
                .thenReturn(updatedTraineeTrainerList);

        mockMvc.perform(put(URL_TEMPLATE + URL_TRAINEE_USERNAME + URL_UPDATE_TRAINERS, TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsernameList\":[\"Joe.Johnson\", \"Peter.Peterson\"]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {ROLE_ADMIN})
    void toggleTraineeActivation() throws Exception {
        when(trainerService.toggleTrainerActivation(USERNAME, true)).thenReturn(true);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void toggleTrainerActivationReturnsBadRequestWhenToggleUnsuccessful() throws Exception {
        when(trainerService.toggleTrainerActivation(anyString(), anyBoolean())).thenReturn(false);

        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isUnauthorized());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }

    @Test
    @WithMockUser(username = USERNAME, authorities = {ROLE_TEST})
    void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().is3xxRedirection());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnUnAuthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(patch(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param(PARAM_USERNAME, USERNAME)
                        .param(PARAM_IS_ACTIVE, ACTIVE_STATUS))
                .andExpect(status().isUnauthorized());

        verify(trainerService, never()).toggleTrainerActivation(anyString(), anyBoolean());
    }
}

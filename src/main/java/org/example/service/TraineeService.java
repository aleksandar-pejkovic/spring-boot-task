package org.example.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dao.TraineeDAO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.utils.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TraineeService {

    private final TraineeDAO traineeDAO;

    private final CredentialsGenerator generator;

    @Autowired
    public TraineeService(TraineeDAO traineeDAO, CredentialsGenerator credentialsGenerator) {
        this.traineeDAO = traineeDAO;
        this.generator = credentialsGenerator;
    }

    @Transactional
    public Trainee createTrainee(String firstName, String lastName, Date dateOfBirth, String address) {
        User newUser = buildNewUser(firstName, lastName);
        Trainee newTrainee = buildNewTrainee(dateOfBirth, address, newUser);
        String username = generator.generateUsername(newTrainee.getUser());
        String password = generator.generateRandomPassword();
        newTrainee.setUsername(username);
        newTrainee.setPassword(password);
        Trainee savedTrainee = traineeDAO.saveTrainee(newTrainee);
        log.info("Trainee successfully created");
        return savedTrainee;
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        Trainee trainee = traineeDAO.findTraineeByUsername(username);
        log.info("Trainee successfully retrieved");
        return trainee;
    }

    @Transactional
    public Trainee changePassword(CredentialsUpdateDTO credentialsUpdateDTO) {
        Trainee trainee = getTraineeByUsername(credentialsUpdateDTO.getUsername());
        trainee.setPassword(credentialsUpdateDTO.getNewPassword());
        Trainee updatedTrainee = traineeDAO.updateTrainee(trainee);
        log.info("Password successfully changed");
        return updatedTrainee;
    }

    @Transactional
    public Trainee updateTrainee(TraineeUpdateDTO traineeUpdateDTO) {
        Trainee trainee = getTraineeByUsername(traineeUpdateDTO.getUsername());
        trainee.getUser().setFirstName(traineeUpdateDTO.getFirstName());
        trainee.getUser().setLastName(traineeUpdateDTO.getLastName());
        trainee.setDateOfBirth(traineeUpdateDTO.getDateOfBirth());
        trainee.setAddress(traineeUpdateDTO.getAddress());
        trainee.getUser().setActive(traineeUpdateDTO.isActive());
        Trainee updatedTrainee = traineeDAO.updateTrainee(trainee);
        log.info("Trainee successfully updated");
        return updatedTrainee;
    }

    @Transactional
    public boolean toggleTraineeActivation(String username, boolean isActive) {
        Trainee trainee = traineeDAO.findTraineeByUsername(username);
        trainee.getUser().setActive(isActive);
        Trainee updatedTrainee = traineeDAO.updateTrainee(trainee);
        log.info("Activation status successfully updated");
        return Optional.ofNullable(updatedTrainee).isPresent();
    }

    @Transactional
    public boolean deleteTrainee(String username) {
        boolean deletionResult = traineeDAO.deleteTraineeByUsername(username);
        if (deletionResult) {
            log.info("Trainee successfully deleted");
        } else {
            log.warn("Trainee deletion failed. No such Trainee found.");
        }
        return deletionResult;
    }

    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        List<Trainee> trainees = traineeDAO.getAllTrainees();
        log.info("Successfully retrieved all Trainees");
        return trainees;
    }

    private User buildNewUser(String firstName, String lastName) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private Trainee buildNewTrainee(Date dateOfBirth, String address, User newUser) {
        return Trainee.builder()
                .dateOfBirth(dateOfBirth)
                .address(address)
                .user(newUser)
                .build();
    }
}

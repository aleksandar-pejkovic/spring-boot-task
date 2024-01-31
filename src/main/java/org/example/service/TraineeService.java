package org.example.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainee.TraineeUpdateDTO;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TraineeNotFoundException;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.TraineeRepository;
import org.example.utils.credentials.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TraineeService {

    private final TraineeRepository traineeRepository;

    private final CredentialsGenerator generator;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, CredentialsGenerator credentialsGenerator) {
        this.traineeRepository = traineeRepository;
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
        Trainee savedTrainee = traineeRepository.save(newTrainee);
        log.info("Trainee successfully created");
        return savedTrainee;
    }

    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));
        log.info("Trainee successfully retrieved");
        return trainee;
    }

    @Transactional
    public Trainee changePassword(CredentialsUpdateDTO credentialsUpdateDTO) {
        Trainee trainee = getTraineeByUsername(credentialsUpdateDTO.getUsername());
        if (!credentialsUpdateDTO.getOldPassword().equals(trainee.getPassword())) {
            throw new IncorrectPasswordException("Wrong password!");
        } else if (credentialsUpdateDTO.getNewPassword().equals(trainee.getPassword())) {
            throw new IdenticalPasswordException("New password must be different than old password");
        }
        trainee.setPassword(credentialsUpdateDTO.getNewPassword());
        Trainee updatedTrainee = traineeRepository.save(trainee);
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
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee successfully updated");
        return updatedTrainee;
    }

    @Transactional
    public boolean toggleTraineeActivation(String username, boolean isActive) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException("Trainee not found"));
        trainee.getUser().setActive(isActive);
        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Activation status successfully updated");
        return Optional.ofNullable(updatedTrainee).isPresent();
    }

    @Transactional
    public boolean deleteTrainee(String username) {
        boolean deletionResult = traineeRepository.deleteByUserUsername(username);
        if (deletionResult) {
            log.info("Trainee successfully deleted");
            return true;
        } else {
            log.warn("Trainee deletion failed. No such Trainee found.");
            throw new TraineeNotFoundException("Trainee not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        List<Trainee> trainees = traineeRepository.findAll();
        log.info("Successfully retrieved all Trainees");
        return trainees;
    }

    private User buildNewUser(String firstName, String lastName) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
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

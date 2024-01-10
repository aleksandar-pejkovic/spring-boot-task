package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerListDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.utils.CredentialsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrainerService {

    private final TrainerDAO trainerDAO;

    private final TraineeDAO traineeDAO;

    private final CredentialsGenerator generator;

    private final TrainingDAO trainingDAO;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, TraineeDAO traineeDAO, CredentialsGenerator credentialsGenerator,
                          TrainingDAO trainingDAO) {
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
        this.generator = credentialsGenerator;
        this.trainingDAO = trainingDAO;
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, TrainingTypeName specialization) {
        TrainingType trainingType = trainingDAO.findTrainingTypeByName(specialization);
        User newUser = buildNewUser(firstName, lastName);
        Trainer newTrainer = buildNewTrainer(newUser, trainingType);
        String username = generator.generateUsername(newTrainer.getUser());
        String password = generator.generateRandomPassword();
        newTrainer.setUsername(username);
        newTrainer.setPassword(password);
        Trainer savedTrained = trainerDAO.saveTrainer(newTrainer);
        log.info("Trainer successfully saved");
        return savedTrained;
    }

    @Transactional(readOnly = true)
    public Trainer getTrainerByUsername(String username) {
        Trainer trainer = trainerDAO.findTrainerByUsername(username);
        log.info("Successfully retrieved trainer by username");
        return trainer;
    }

    @Transactional
    public Trainer changePassword(CredentialsUpdateDTO credentialsUpdateDTO) {
        Trainer trainer = getTrainerByUsername(credentialsUpdateDTO.getUsername());
        trainer.setPassword(credentialsUpdateDTO.getNewPassword());
        Trainer updatedTrainer = trainerDAO.updateTrainer(trainer);
        log.info("Password successfully updated");
        return updatedTrainer;
    }

    @Transactional
    public Trainer updateTrainer(TrainerUpdateDTO trainerUpdateDTO) {
        Trainer trainer = getTrainerByUsername(trainerUpdateDTO.getUsername());
        TrainingType trainingType = trainingDAO.findTrainingTypeByName(trainerUpdateDTO.getSpecialization());
        trainer.getUser().setFirstName(trainerUpdateDTO.getFirstName());
        trainer.getUser().setLastName(trainerUpdateDTO.getLastName());
        trainer.setSpecialization(trainingType);
        trainer.getUser().setActive(trainerUpdateDTO.isActive());
        Trainer updatedTrainer = trainerDAO.updateTrainer(trainer);
        log.info("Trainer successfully updated");
        return updatedTrainer;
    }

    public boolean toggleTrainerActivation(String username, boolean isActive) {
        Trainer trainer = trainerDAO.findTrainerByUsername(username);
        trainer.getUser().setActive(isActive);
        Trainer updatedTrainer = trainerDAO.updateTrainer(trainer);
        if (Optional.ofNullable(updatedTrainer).isPresent()) {
            log.info("Trainer's activation status successfully updated");
            return true;
        } else {
            log.info("Activation status update failed. Trainer not found.");
            return false;
        }
    }

    @Transactional
    public boolean deleteTrainer(String username, String password) {
        boolean deletionResult = trainerDAO.deleteTrainerByUsername(username);
        if (deletionResult) {
            log.info("Trainer successfully deleted");
            return true;
        } else {
            log.info("Trainer deletion failed");
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainerList(String traineeUsername) {
        List<Trainer> unassignedTrainers = trainerDAO.getNotAssignedTrainers(traineeUsername);
        log.info("Successfully retrieved unassigned trainers");
        return unassignedTrainers;
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = trainerDAO.getAllTrainers();
        log.info("Successfully retrieved all trainers");
        return trainers;
    }

    @Transactional
    public List<Trainer> updateTraineeTrainerList(String traineeUsername, TrainerListDTO trainerListDTO) {
        Trainee trainee = traineeDAO.findTraineeByUsername(traineeUsername);
        List<Trainer> trainers = trainerDAO.getAllTrainers().stream()
                .filter(trainer -> trainerListDTO.getTrainerUsernameList().contains(trainer.getUsername()))
                .toList();
        trainee.getTrainerList().addAll(trainers);
        Trainee updatedTrainee = traineeDAO.updateTrainee(trainee);
        log.info("Successfully updated trainee's trainers list");
        return updatedTrainee.getTrainerList();
    }

    private User buildNewUser(String firstName, String lastName) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private Trainer buildNewTrainer(User newUser, TrainingType specialization) {
        return Trainer.builder()
                .user(newUser)
                .specialization(specialization)
                .build();
    }
}

package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
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

    private final TrainerRepository trainerRepository;

    private final TraineeRepository traineeRepository;

    private final CredentialsGenerator generator;

    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository, TraineeRepository traineeRepository, CredentialsGenerator credentialsGenerator,
                          TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.generator = credentialsGenerator;
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, TrainingTypeName specialization) {
        TrainingType trainingType = trainingRepository.findTrainingTypeByName(specialization);
        User newUser = buildNewUser(firstName, lastName);
        Trainer newTrainer = buildNewTrainer(newUser, trainingType);
        String username = generator.generateUsername(newTrainer.getUser());
        String password = generator.generateRandomPassword();
        newTrainer.setUsername(username);
        newTrainer.setPassword(password);
        Trainer savedTrained = trainerRepository.save(newTrainer);
        log.info("Trainer successfully saved");
        return savedTrained;
    }

    @Transactional(readOnly = true)
    public Trainer getTrainerByUsername(String username) {
        Trainer trainer = trainerRepository.findTrainerByUsername(username);
        log.info("Successfully retrieved trainer by username");
        return trainer;
    }

    @Transactional
    public Trainer changePassword(CredentialsUpdateDTO credentialsUpdateDTO) {
        Trainer trainer = getTrainerByUsername(credentialsUpdateDTO.getUsername());
        trainer.setPassword(credentialsUpdateDTO.getNewPassword());
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Password successfully updated");
        return updatedTrainer;
    }

    @Transactional
    public Trainer updateTrainer(TrainerUpdateDTO trainerUpdateDTO) {
        Trainer trainer = getTrainerByUsername(trainerUpdateDTO.getUsername());
        TrainingType trainingType = trainingRepository.findTrainingTypeByName(trainerUpdateDTO.getSpecialization());
        trainer.getUser().setFirstName(trainerUpdateDTO.getFirstName());
        trainer.getUser().setLastName(trainerUpdateDTO.getLastName());
        trainer.setSpecialization(trainingType);
        trainer.getUser().setActive(trainerUpdateDTO.isActive());
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Trainer successfully updated");
        return updatedTrainer;
    }

    public boolean toggleTrainerActivation(String username, boolean isActive) {
        Trainer trainer = trainerRepository.findTrainerByUsername(username);
        trainer.getUser().setActive(isActive);
        Trainer updatedTrainer = trainerRepository.save(trainer);
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
        boolean deletionResult = trainerRepository.deleteTrainerByUsername(username);
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
        List<Trainer> unassignedTrainers = trainerRepository.getNotAssignedTrainers(traineeUsername);
        log.info("Successfully retrieved unassigned trainers");
        return unassignedTrainers;
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = trainerRepository.findAll();
        log.info("Successfully retrieved all trainers");
        return trainers;
    }

    @Transactional
    public List<Trainer> updateTraineeTrainerList(String traineeUsername, TrainerListDTO trainerListDTO) {
        Trainee trainee = traineeRepository.findTraineeByUsername(traineeUsername);
        List<Trainer> trainers = trainerRepository.findAll().stream()
                .filter(trainer -> trainerListDTO.getTrainerUsernameList().contains(trainer.getUsername()))
                .toList();
        trainee.getTrainerList().addAll(trainers);
        Trainee updatedTrainee = traineeRepository.save(trainee);
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

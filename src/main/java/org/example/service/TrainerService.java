package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.dto.credentials.CredentialsUpdateDTO;
import org.example.dto.trainer.TrainerListDTO;
import org.example.dto.trainer.TrainerUpdateDTO;
import org.example.enums.TrainingTypeName;
import org.example.exception.credentials.IdenticalPasswordException;
import org.example.exception.credentials.IncorrectPasswordException;
import org.example.exception.notfound.TrainerNotFoundException;
import org.example.exception.notfound.TrainingTypeNotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.utils.credentials.CredentialsGenerator;
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

    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository, TraineeRepository traineeRepository,
                          CredentialsGenerator credentialsGenerator, TrainingTypeRepository trainingTypeRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.generator = credentialsGenerator;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, TrainingTypeName specialization) {
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(specialization)
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found"));
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
        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
        log.info("Successfully retrieved trainer by username");
        return trainer;
    }

    @Transactional
    public Trainer changePassword(CredentialsUpdateDTO credentialsUpdateDTO) {
        Trainer trainer = getTrainerByUsername(credentialsUpdateDTO.getUsername());
        if (!credentialsUpdateDTO.getOldPassword().equals(trainer.getPassword())) {
            throw new IncorrectPasswordException("Wrong password!");
        } else if (credentialsUpdateDTO.getNewPassword().equals(trainer.getPassword())) {
            throw new IdenticalPasswordException("New password must be different than old password");
        }
        trainer.setPassword(credentialsUpdateDTO.getNewPassword());
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Password successfully updated");
        return updatedTrainer;
    }

    @Transactional
    public Trainer updateTrainer(TrainerUpdateDTO trainerUpdateDTO) {
        Trainer trainer = getTrainerByUsername(trainerUpdateDTO.getUsername());
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(trainerUpdateDTO.getSpecialization())
                .orElseThrow(() -> new TrainingTypeNotFoundException("Training type not found"));
        trainer.getUser().setFirstName(trainerUpdateDTO.getFirstName());
        trainer.getUser().setLastName(trainerUpdateDTO.getLastName());
        trainer.setSpecialization(trainingType);
        trainer.getUser().setActive(trainerUpdateDTO.isActive());
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Trainer successfully updated");
        return updatedTrainer;
    }

    public boolean toggleTrainerActivation(String username, boolean isActive) {
        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer type not found"));
        trainer.getUser().setActive(isActive);
        Trainer updatedTrainer = trainerRepository.save(trainer);
        log.info("Activation status successfully updated");
        return Optional.ofNullable(updatedTrainer).isPresent();
    }

    @Transactional
    public boolean deleteTrainer(String username) {
        boolean deletionResult = trainerRepository.deleteByUserUsername(username);
        if (deletionResult) {
            log.info("Trainer successfully deleted");
            return true;
        } else {
            log.info("Trainer deletion failed");
            throw new TrainerNotFoundException("Trainer not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Trainer> getNotAssignedTrainerList(String traineeUsername) {
        List<Trainer> unassignedTrainers = trainerRepository.findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(traineeUsername);
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
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
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

package org.example.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.dto.training.TrainingCreateDTO;
import org.example.dto.trainingType.TrainingTypeDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.utils.TrainingTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrainingService {

    private final TrainingRepository trainingRepository;

    private final TraineeRepository traineeRepository;

    private final TrainerRepository trainerRepository;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public boolean createTraining(TrainingCreateDTO trainingCreateDTO) {

        Trainee trainee = traineeRepository.findTraineeByUsername(trainingCreateDTO.getTraineeUsername());
        Trainer trainer = trainerRepository.findTrainerByUsername(trainingCreateDTO.getTrainerUsername());
        trainer.getTraineeList().add(trainee);
        trainee.getTrainerList().add(trainer);

        TrainingType trainingType = trainingRepository.findTrainingTypeByName(trainingCreateDTO.getTrainingTypeName());

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingType.getTrainingTypeName().name())
                .trainingType(trainingType)
                .trainingDate(trainingCreateDTO.getTrainingDate())
                .trainingDuration(trainingCreateDTO.getTrainingDuration())
                .build();

        Training savedTraining = trainingRepository.save(training);
        log.info("Training successfully created");
        return Optional.ofNullable(savedTraining).isPresent();
    }

    @Transactional(readOnly = true)
    public Training getTrainingById(long id) {
        Training training = trainingRepository.findById(id).orElseThrow();
        log.info("Training successfully retrieved by id");
        return training;
    }

    @Transactional
    public Training updateTraining(Training training) {
        Training updatedTraining = trainingRepository.save(training);
        log.info("Training successfully updated");
        return updatedTraining;
    }

    @Transactional
    public boolean deleteTraining(Training training) {
        Trainee trainee = training.getTrainee();
        Trainer trainer = training.getTrainer();
        trainer.getTraineeList().remove(trainee);
        trainee.getTrainerList().remove(trainer);
        try {
            trainingRepository.delete(training);
            log.info("Training successfully deleted");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingList(String username,
                                                 Date periodFrom,
                                                 Date periodTo,
                                                 String trainerName,
                                                 String trainingTypeName) {
        List<Training> trainingList = trainingRepository.getTraineeTrainingList(username, periodFrom, periodTo,
                trainerName, trainingTypeName);
        log.info("Successfully retrieved trainee's training list");
        return trainingList;
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingList(String username,
                                                 Date periodFrom,
                                                 Date periodTo,
                                                 String traineeName) {
        List<Training> trainingList = trainingRepository.getTrainerTrainingList(username, periodFrom, periodTo, traineeName);
        log.info("Successfully retrieved trainer's training list");
        return trainingList;
    }

    @Transactional(readOnly = true)
    public List<Training> getAllTrainings() {
        List<Training> trainings = trainingRepository.findAll();
        log.info("Retrieved all trainings successfully");
        return trainings;
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDTO> finaAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingRepository.findAllTrainingTypes();
        log.info("Retrieved all training types successfully");
        return TrainingTypeConverter.convertToDtoList(trainingTypes);
    }
}

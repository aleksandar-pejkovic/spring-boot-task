package org.example.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
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

    private final TrainingDAO trainingDAO;

    private final TraineeDAO traineeDAO;

    private final TrainerDAO trainerDAO;

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, TraineeDAO traineeDao, TrainerDAO trainerDAO) {
        this.trainingDAO = trainingDAO;
        this.traineeDAO = traineeDao;
        this.trainerDAO = trainerDAO;
    }

    @Transactional
    public boolean createTraining(TrainingCreateDTO trainingCreateDTO) {

        Trainee trainee = traineeDAO.findTraineeByUsername(trainingCreateDTO.getTraineeUsername());
        Trainer trainer = trainerDAO.findTrainerByUsername(trainingCreateDTO.getTrainerUsername());
        trainer.getTraineeList().add(trainee);
        trainee.getTrainerList().add(trainer);

        TrainingType trainingType = trainingDAO.findTrainingTypeByName(trainingCreateDTO.getTrainingTypeName());

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingType.getTrainingTypeName().name())
                .trainingType(trainingType)
                .trainingDate(trainingCreateDTO.getTrainingDate())
                .trainingDuration(trainingCreateDTO.getTrainingDuration())
                .build();

        Training savedTraining = trainingDAO.saveTraining(training);
        log.info("Training successfully created");
        return Optional.ofNullable(savedTraining).isPresent();
    }

    @Transactional(readOnly = true)
    public Training getTrainingById(long id) {
        Training training = trainingDAO.findById(id);
        log.info("Training successfully retrieved by id");
        return training;
    }

    @Transactional
    public Training updateTraining(Training training) {
        Training updatedTraining = trainingDAO.updateTraining(training);
        log.info("Training successfully updated");
        return updatedTraining;
    }

    @Transactional
    public boolean deleteTraining(Training training) {
        Trainee trainee = training.getTrainee();
        Trainer trainer = training.getTrainer();
        trainer.getTraineeList().remove(trainee);
        trainee.getTrainerList().remove(trainer);
        boolean result = trainingDAO.deleteTraining(training);
        log.info("Training successfully deleted");
        return result;
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingList(String username,
                                                 Date periodFrom,
                                                 Date periodTo,
                                                 String trainerName,
                                                 String trainingTypeName) {
        List<Training> trainingList = trainingDAO.getTraineeTrainingList(username, periodFrom, periodTo,
                trainerName, trainingTypeName);
        log.info("Successfully retrieved trainee's training list");
        return trainingList;
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingList(String username,
                                                 Date periodFrom,
                                                 Date periodTo,
                                                 String traineeName) {
        List<Training> trainingList = trainingDAO.getTrainerTrainingList(username, periodFrom, periodTo, traineeName);
        log.info("Successfully retrieved trainer's training list");
        return trainingList;
    }

    @Transactional(readOnly = true)
    public List<Training> getAllTrainings() {
        List<Training> trainings = trainingDAO.findAllTrainings();
        log.info("Retrieved all trainings successfully");
        return trainings;
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDTO> finaAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingDAO.findAllTrainingTypes();
        log.info("Retrieved all training types successfully");
        return TrainingTypeConverter.convertToDtoList(trainingTypes);
    }
}

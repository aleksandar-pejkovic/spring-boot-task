package org.example.utils.dummydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.model.Training;

public class TrainingDummyDataFactory {

    private static final int DEFAULT_TRAINING_DURATION = 30;
    public static final Date DEFAULT_TRAINING_DATE = new Date();

    private TrainingDummyDataFactory() {
    }

    public static Training getTrainingUnderTest() {
        return Training.builder()
                .trainee(TraineeDummyDataFactory.getTraineeForTrainingUnderTest())
                .trainer(TrainerDummyDataFactory.getTrainerForTrainingUnderTest())
                .trainingType(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .trainingDate(DEFAULT_TRAINING_DATE)
                .trainingDuration(DEFAULT_TRAINING_DURATION)
                .build();
    }

    public static List<Training> getTrainingsForTrainerUnderTest() {
        return getTrainings();
    }

    public static List<Training> getTrainingsForTrainee() {
        return getTrainings();
    }

    public static List<Training> getTrainingsForTrainer() {
        return getTrainings();
    }

    private static ArrayList<Training> getTrainings() {
        Training training1 = Training.builder()
                .trainee(TraineeDummyDataFactory.getSimpleTraineeWithUser())
                .trainer(TrainerDummyDataFactory.getSimpleTrainerWithUser())
                .trainingType(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .trainingDate(DEFAULT_TRAINING_DATE)
                .trainingDuration(DEFAULT_TRAINING_DURATION)
                .build();

        Training training2 = Training.builder()
                .trainee(TraineeDummyDataFactory.getSimpleTraineeWithUser())
                .trainer(TrainerDummyDataFactory.getSimpleTrainerWithUser())
                .trainingType(TrainingTypeDummyDataFactory.getTrainingTypeStrength())
                .trainingDate(DEFAULT_TRAINING_DATE)
                .trainingDuration(DEFAULT_TRAINING_DURATION)
                .build();

        return new ArrayList<>(List.of(training1, training2));
    }
}

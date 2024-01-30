package org.example.utils.dummydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.model.Trainer;

public class TrainerDummyDataFactory {

    private static final int DEFAULT_TRAINING_DURATION = 30;
    private static final String DEFAULT_ADDRESS = "11000 Belgrade";
    private static final Date DATE_OF_BIRTH = new Date();
    private static final Date DEFAULT_TRAINING_DATE = new Date();

    private TrainerDummyDataFactory() {
    }

    public static Trainer getTrainerUnderTestingJoeJohnson() {
        return Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .user(UserDummyDataFactory.getUserJoeJohnson())
                .traineeList(TraineeDummyDataFactory.getTraineesForTrainerUnderTest())
                .trainingList(TrainingDummyDataFactory.getTrainingsForTrainerUnderTest())
                .build();
    }

    public static List<Trainer> getTrainersForTraineeUnderTest() {
        Trainer trainer1 = Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .user(UserDummyDataFactory.getUserJoeJohnson())
                .build();

        Trainer trainer2 = Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeStrength())
                .user(UserDummyDataFactory.getUserPeterPeterson())
                .build();
        return new ArrayList<>(List.of(trainer1, trainer2));
    }

    public static Trainer getTrainerForTrainingUnderTest() {
        return Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .user(UserDummyDataFactory.getUserJoeJohnson())
                .traineeList(TraineeDummyDataFactory.getTraineesForTrainerUnderTest())
                .trainingList(TrainingDummyDataFactory.getTrainingsForTrainerUnderTest())
                .build();
    }
}

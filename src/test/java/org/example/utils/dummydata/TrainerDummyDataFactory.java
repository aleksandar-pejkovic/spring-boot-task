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
        return getTrainers();
    }

    public static Trainer getTrainerForTrainingUnderTest() {
        return Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .user(UserDummyDataFactory.getUserJoeJohnson())
                .traineeList(TraineeDummyDataFactory.getTraineesForTrainerUnderTest())
                .trainingList(TrainingDummyDataFactory.getTrainingsForTrainerUnderTest())
                .build();
    }

    public static List<Trainer> getUnassignedTrainers() {
        return getTrainers();
    }


    public static List<Trainer> getUpdatedTrainerListForTrainee() {
        return getTrainers();
    }

    public static Trainer getSimpleTrainerWithUser() {
        return Trainer.builder()
                .user(UserDummyDataFactory.getUserPeterPeterson())
                .build();
    }

    private static ArrayList<Trainer> getTrainers() {
        Trainer trainer1 = getTrainerJoeJohnsonForAerobic();
        Trainer trainer2 = getTrainerPeterPetersonForStrength();
        return new ArrayList<>(List.of(trainer1, trainer2));
    }

    private static Trainer getTrainerJoeJohnsonForAerobic() {
        return Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeAerobic())
                .user(UserDummyDataFactory.getUserJoeJohnson())
                .build();
    }

    private static Trainer getTrainerPeterPetersonForStrength() {
        return Trainer.builder()
                .specialization(TrainingTypeDummyDataFactory.getTrainingTypeStrength())
                .user(UserDummyDataFactory.getUserPeterPeterson())
                .build();
    }
}

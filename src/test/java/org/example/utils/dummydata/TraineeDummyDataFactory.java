package org.example.utils.dummydata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.model.Trainee;

public class TraineeDummyDataFactory {

    private static final Date DATE_OF_BIRTH = new Date();
    private static final String DEFAULT_ADDRESS = "11000 Belgrade";

    private TraineeDummyDataFactory() {
    }

    public static Trainee getTraineeUnderTestJohnDoe() {
        return Trainee.builder()
                .user(UserDummyDataFactory.getUserJohnDoe())
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .trainerList(TrainerDummyDataFactory.getTrainersForTraineeUnderTest())
                .build();
    }

    public static List<Trainee> getTraineesForTrainerUnderTest() {
        Trainee trainee1 = Trainee.builder()
                .user(UserDummyDataFactory.getUserJohnDoe())
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .build();

        Trainee trainee2 = Trainee.builder()
                .user(UserDummyDataFactory.getUserPeterPeterson())
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .build();
        return new ArrayList<>(List.of(trainee1, trainee2));
    }

    public static Trainee getTraineeForTrainingUnderTest() {
        return Trainee.builder()
                .user(UserDummyDataFactory.getUserJohnDoe())
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .trainerList(TrainerDummyDataFactory.getTrainersForTraineeUnderTest())
                .build();
    }

    public static Trainee getSimpleTraineeWithUser() {
        return Trainee.builder()
                .user(UserDummyDataFactory.getUserJohnDoe())
                .build();
    }
}

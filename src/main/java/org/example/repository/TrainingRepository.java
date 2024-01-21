package org.example.repository;

import java.util.Date;
import java.util.List;

import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends ListCrudRepository<Training, Long> {

    List<Training> findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameAndTrainingTypeTrainingTypeName(
            String username, Date periodFrom, Date periodTo, String trainerName, String trainingTypeName);

    List<Training> findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsername(
            String username, Date periodFrom, Date periodTo, String traineeName);
}

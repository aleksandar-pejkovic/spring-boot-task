package org.example.repository;

import java.util.Date;
import java.util.List;

import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends ListCrudRepository<Training, Long> {

    @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :username " +
            "AND t.trainingDate > :periodFrom AND t.trainingDate < :periodTo " +
            "AND t.trainer.user.username = :trainerName " +
            "AND t.trainingType.trainingTypeName = :trainingTypeName")
    List<Training> getTraineeTrainingList(@Param("username") String username,
                                          @Param("periodFrom") Date periodFrom,
                                          @Param("periodTo") Date periodTo,
                                          @Param("trainerName") String trainerName,
                                          @Param("trainingTypeName") String trainingTypeName);

    @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :username " +
            "AND t.trainingDate > :periodFrom AND t.trainingDate < :periodTo " +
            "AND t.trainee.user.username = :traineeName")
    List<Training> getTrainerTrainingList(@Param("username") String username,
                                          @Param("periodFrom") Date periodFrom,
                                          @Param("periodTo") Date periodTo,
                                          @Param("traineeName") String traineeName);

    @Query("SELECT t FROM Training t WHERE t.trainingType.trainingTypeName = :trainingTypeName")
    TrainingType findTrainingTypeByName(@Param("trainingTypeName") TrainingTypeName trainingTypeName);

    @Query("SELECT t FROM TrainingType t")
    List<TrainingType> findAllTrainingTypes();
}

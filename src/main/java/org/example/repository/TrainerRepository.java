package org.example.repository;

import java.util.List;

import org.example.model.Trainer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends ListCrudRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t WHERE t.user.username = :username")
    Trainer findTrainerByUsername(String username);

    @Query("DELETE FROM Trainer t WHERE t.user.username = :username")
    boolean deleteTrainerByUsername(String username);

    @Query("SELECT t FROM Trainer t "
            + "LEFT JOIN t.traineeList te "
            + "WHERE te IS NULL "
            + "OR te.user.username = :traineeUsername "
            + "AND t.user.isActive = true")
    List<Trainer> getNotAssignedTrainers(String traineeUsername);
}

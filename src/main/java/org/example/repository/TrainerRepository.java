package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.model.Trainer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends ListCrudRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    boolean deleteByUserUsername(String username);

//    @Query("SELECT t FROM Trainer t "
//            + "LEFT JOIN t.traineeList te "
//            + "WHERE te IS NULL "
//            + "OR te.user.username = :traineeUsername "
//            + "AND t.user.isActive = true")
    List<Trainer> findByTraineeListUserUsernameAndUserIsActiveIsTrueOrTraineeListIsNull(String traineeUsername);
}

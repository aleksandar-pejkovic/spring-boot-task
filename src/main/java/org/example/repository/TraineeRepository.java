package org.example.repository;

import org.example.model.Trainee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends ListCrudRepository<Trainee, Long> {

    @Query("SELECT t FROM Trainee t WHERE t.user.username = :username")
    Trainee findTraineeByUsername(@Param("username") String username);

    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    boolean deleteTraineeByUsername(@Param("username") String username);
}

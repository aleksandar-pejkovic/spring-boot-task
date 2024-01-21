package org.example.repository;

import java.util.Optional;

import org.example.model.Trainee;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends ListCrudRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    boolean deleteByUserUsername(String username);
}

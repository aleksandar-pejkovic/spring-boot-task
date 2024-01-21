package org.example.repository;

import java.util.Optional;

import org.example.enums.TrainingTypeName;
import org.example.model.TrainingType;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends ListCrudRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingTypeName(TrainingTypeName trainingTypeName);
}

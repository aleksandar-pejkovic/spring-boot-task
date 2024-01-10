package org.example.dao;

import java.util.List;

import org.example.model.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeDAO extends AbstractDAO<Trainee> {

    @Autowired
    public TraineeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Trainee saveTrainee(Trainee trainee) {
        return save(trainee);
    }

    public Trainee findTraineeByUsername(String username) {
        return findByUsername(username, Trainee.class);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return update(trainee);
    }

    public boolean deleteTraineeByUsername(String username) {
        int rowsDeleted = deleteByUsername(username, Trainee.class);
        return rowsDeleted >= 1;
    }

    public List<Trainee> getAllTrainees() {
        return findAll(Trainee.class);
    }
}

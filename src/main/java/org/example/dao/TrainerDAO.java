package org.example.dao;

import java.util.List;

import org.example.model.Trainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerDAO extends AbstractDAO<Trainer> {

    @Autowired
    public TrainerDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Trainer saveTrainer(Trainer trainer) {
        return save(trainer);
    }

    public Trainer findTrainerByUsername(String username) {
        return findByUsername(username, Trainer.class);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return update(trainer);
    }

    public boolean deleteTrainerByUsername(String username) {
        int rowsDeleted = deleteByUsername(username, Trainer.class);
        return rowsDeleted >= 1;
    }

    public List<Trainer> getNotAssignedTrainers(String traineeUsername) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "SELECT t FROM Trainer t "
                + "LEFT JOIN t.traineeList te "
                + "WHERE te IS NULL "
                + "OR te.user.username = :traineeUsername "
                + "AND t.user.isActive = true";

        Query<Trainer> query = session.createQuery(hql, Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);

        return query.getResultList();
    }

    public List<Trainer> getAllTrainers() {
        return findAll(Trainer.class);
    }
}

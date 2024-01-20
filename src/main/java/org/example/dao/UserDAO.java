package org.example.dao;

import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends AbstractDAO<User> {

    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            String hqlQuery = "FROM User u where u.username = :username";
            Query<User> query = session.createQuery(hqlQuery, User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        }
    }
}

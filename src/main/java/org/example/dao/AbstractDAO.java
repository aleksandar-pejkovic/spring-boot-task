package org.example.dao;

import java.util.List;

import org.example.model.AbstractEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

@Getter
public abstract class AbstractDAO<T extends AbstractEntity> {

    protected final SessionFactory sessionFactory;

    @Autowired
    protected AbstractDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected T save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.persist(entity);
            return entity;
        }
    }

    protected T findById(Class<T> entityClass, long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entityClass, id);
        }
    }

    protected T findByUsername(String username, Class<T> entityClass) {
        try (Session session = sessionFactory.openSession()) {
            String entityClassName = entityClass.getCanonicalName();
            String hqlQuery = String.format("FROM %s t where t.user.username = :username", entityClassName);
            Query<T> query = session.createQuery(hqlQuery, entityClass);
            query.setParameter("username", username);
            return query.getSingleResult();
        }
    }

    protected T update(T entity) {
        try (Session session = sessionFactory.openSession()) {
            return session.merge(entity);
        }
    }

    protected boolean delete(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.remove(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected int deleteByUsername(String username, Class<T> entityClass) {
        try (Session session = sessionFactory.openSession()) {
            String entityClassName = entityClass.getCanonicalName();
            String hqlQuery = String.format("DELETE FROM %s t WHERE t.user.username = :username", entityClassName);
            Query<Long> query = session.createQuery(hqlQuery, Long.class);
            query.setParameter("username", username);

            return query.executeUpdate();
        }
    }

    protected List<T> findAll(Class<T> entityClass) {
        try (Session session = sessionFactory.openSession()) {
            String entityClassName = entityClass.getCanonicalName();
            String queryHQL = "FROM " + entityClassName;
            Query<T> query = session.createQuery(queryHQL, entityClass);
            return query.getResultList();
        }
    }
}

package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.example.enums.TrainingTypeName;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TrainerDAOTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private TrainerDAO trainerDAO;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() throws Exception {
        try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
            when(sessionFactory.getCurrentSession()).thenReturn(session);
            testTrainer = Trainer.builder()
                    .specialization(TrainingType.builder()
                            .id(1L)
                            .trainingTypeName(TrainingTypeName.AEROBIC)
                            .build())
                    .user(User.builder()
                            .isActive(true)
                            .lastName("Smith")
                            .firstName("John")
                            .username("John.Smith")
                            .password("password123")
                            .build())
                    .build();
        }
    }

    @Test
    void saveTrainer() {
        // Arrange
        doNothing().when(session).persist(testTrainer);

        // Act
        Trainer savedTrainer = trainerDAO.saveTrainer(testTrainer);

        // Assert
        assertEquals(testTrainer.getUsername(), savedTrainer.getUsername());
        assertEquals(testTrainer.getUser().getFirstName(), savedTrainer.getUser().getFirstName());
        verify(session, times(1)).persist(testTrainer);
    }

    @Test
    void findTrainerByUsername() {
        // Arrange
        Query<Trainer> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(testTrainer);

        // Act
        Trainer foundTrainer = trainerDAO.findTrainerByUsername("John.Smith");

        // Assert
        assertEquals(testTrainer, foundTrainer);
        verify(session, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).getSingleResult();
    }

    @Test
    void updateTrainer() {
        // Arrange
        Trainer trainerBeforeUpdate = testTrainer;
        Trainer trainerAfterUpdate = Trainer.builder()
                .specialization(TrainingType.builder()
                        .id(2L)
                        .trainingTypeName(TrainingTypeName.AEROBIC)
                        .build())
                .user(testTrainer.getUser())
                .build();

        when(session.merge(trainerBeforeUpdate)).thenReturn(trainerAfterUpdate);

        // Act
        Trainer resultTrainer = trainerDAO.updateTrainer(trainerBeforeUpdate);

        // Assert
        assertEquals(trainerAfterUpdate, resultTrainer);
        verify(session, times(1)).merge(trainerBeforeUpdate);
    }

    @Test
    void deleteTrainer() {
        // Arrange
        Query<Long> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        boolean result = trainerDAO.deleteTrainerByUsername("John.Smith");

        // Assert
        assertTrue(result);
        verify(session, times(1)).createQuery(anyString(), eq(Long.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    void deleteTrainer_NotFound() {
        // Arrange
        Query<Long> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        // Act
        boolean result = trainerDAO.deleteTrainerByUsername("NonExistentUser");

        // Assert
        assertFalse(result);
        verify(session, times(1)).createQuery(anyString(), eq(Long.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    void getNotAssignedTrainers() {
        // Arrange
        Query<Trainer> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(eq("traineeUsername"), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(testTrainer));

        // Act
        List<Trainer> trainers = trainerDAO.getNotAssignedTrainers("Trainee1");

        // Assert
        assertEquals(1, trainers.size());
        assertEquals(testTrainer, trainers.get(0));
        verify(session, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(query, times(1)).setParameter(eq("traineeUsername"), anyString());
        verify(query, times(1)).getResultList();
    }

    @Test
    void getAllTrainers() {
        // Arrange
        Query<Trainer> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(testTrainer));

        // Act
        List<Trainer> trainers = trainerDAO.getAllTrainers();

        // Assert
        assertEquals(1, trainers.size());
        assertEquals(testTrainer, trainers.get(0));
        verify(session, times(1)).createQuery(anyString(), eq(Trainer.class));
        verify(query, times(1)).getResultList();
    }
}

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

import org.example.model.Trainee;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TraineeDAO.class})
class TraineeDAOTest {

    @MockBean
    private SessionFactory sessionFactory;

    @MockBean
    private Session session;

    @Autowired
    private TraineeDAO traineeDAO;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() throws Exception {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        testTrainee = Trainee.builder()
                .address("11000 Belgrade")
                .dateOfBirth(new java.util.Date())
                .user(User.builder()
                        .isActive(true)
                        .lastName("Biaggi")
                        .firstName("Max")
                        .username("Max.Biaggi")
                        .password("0123456789")
                        .build())
                .build();
    }

    @Test
    void saveTrainee() {
        // Arrange
        doNothing().when(session).persist(eq(testTrainee));

        // Act
        Trainee savedTrainee = traineeDAO.saveTrainee(testTrainee);

        // Assert
        assertEquals(testTrainee.getUsername(), savedTrainee.getUsername());
        assertEquals(testTrainee.getUser().getFirstName(), savedTrainee.getUser().getFirstName());
        verify(session, times(1)).persist(testTrainee);
    }

    @Test
    void findTraineeByUsername() {
        // Arrange
        Query<Trainee> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(testTrainee);

        // Act
        Trainee foundTrainee = traineeDAO.findTraineeByUsername("Max.Biaggi");

        // Assert
        assertEquals(testTrainee, foundTrainee);
        verify(session, times(1)).createQuery(anyString(), eq(Trainee.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).getSingleResult();
    }

    @Test
    void updateTrainee() {
        // Arrange
        Trainee traineeBeforeUpdate = testTrainee;
        Trainee traineeAfterUpdate = Trainee.builder()
                .address("18000 Nis")
                .dateOfBirth(testTrainee.getDateOfBirth())
                .user(testTrainee.getUser())
                .build();

        when(session.merge(traineeBeforeUpdate)).thenReturn(traineeAfterUpdate);

        // Act
        Trainee resultTrainee = traineeDAO.updateTrainee(traineeBeforeUpdate);

        // Assert
        assertEquals(traineeAfterUpdate, resultTrainee);
        verify(session, times(1)).merge(traineeBeforeUpdate);
    }

    @Test
    void deleteTrainee() {
        // Arrange
        Query<Long> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        boolean result = traineeDAO.deleteTraineeByUsername("Max.Biaggi");

        // Assert
        assertTrue(result);
        verify(session, times(1)).createQuery(anyString(), eq(Long.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    void deleteTrainee_NotFound() {
        // Arrange
        Query<Long> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        // Act
        boolean result = traineeDAO.deleteTraineeByUsername("NonExistentUser");

        // Assert
        assertFalse(result);
        verify(session, times(1)).createQuery(anyString(), eq(Long.class));
        verify(query, times(1)).setParameter(eq("username"), anyString());
        verify(query, times(1)).executeUpdate();
    }

    @Test
    void getAllTrainees() {
        // Arrange
        Query<Trainee> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(testTrainee));

        // Act
        List<Trainee> trainees = traineeDAO.getAllTrainees();

        // Assert
        assertEquals(1, trainees.size());
        assertEquals(testTrainee, trainees.get(0));
        verify(session, times(1)).createQuery(anyString(), eq(Trainee.class));
        verify(query, times(1)).getResultList();
    }
}

package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.example.model.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPath;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

class TrainingDAOTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private TrainingDAO trainingDAO;

    @Captor
    ArgumentCaptor<CriteriaQuery<Training>> criteriaQueryCaptor;

    private Training training;

    @BeforeEach
    void setUp() throws Exception {
        try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
            when(sessionFactory.getCurrentSession()).thenReturn(session);
            training = Training.builder()
                    .trainingDuration(10)
                    .build();
        }
    }

    @Test
    void saveTraining() {
        // Arrange
        doNothing().when(session).persist(training);

        // Act
        Training savedTraining = trainingDAO.saveTraining(training);

        // Assert
        assertEquals(training.getId(), savedTraining.getId());
        verify(session, times(1)).persist(training);
    }

    @Test
    void findTrainingById() {
        // Arrange
        when(session.get(Training.class, 1L)).thenReturn(training);

        // Act
        Training foundTraining = trainingDAO.findById(1L);

        // Assert
        assertEquals(training, foundTraining);
    }

//    @Test
//    void getTraineeTrainingList() {
//        // Arrange
//        HibernateCriteriaBuilder criteriaBuilderMock = mock(HibernateCriteriaBuilder.class);
//        JpaCriteriaQuery<Training> criteriaQueryMock = mock(JpaCriteriaQuery.class);
//        JpaRoot<Training> rootMock = mock(JpaRoot.class);
//        JpaPath<Object> trainerPathMock = mock(JpaPath.class);
//        JpaPath<Object> userPathMock = mock(JpaPath.class);
//        JpaPath<Object> usernamePathMock = mock(JpaPath.class);
//        Query<Training> queryMock = mock(Query.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//        List<Training> expectedResult = Collections.singletonList(training);
//
//        when(session.getCriteriaBuilder()).thenReturn(criteriaBuilderMock);
//        when(criteriaBuilderMock.createQuery(Training.class)).thenReturn(criteriaQueryMock);
//        when(criteriaQueryMock.from(Training.class)).thenReturn(rootMock);
//        when(rootMock.get("trainee")).thenReturn(trainerPathMock);
//        when(trainerPathMock.get("user")).thenReturn(userPathMock);
//        when(userPathMock.get("username")).thenReturn(usernamePathMock);
//        when(criteriaQueryMock.select(rootMock)).thenReturn(criteriaQueryMock);
//        when(criteriaQueryMock.where(predicates.toArray(new Predicate[]{}))).thenReturn(criteriaQueryMock);
//        when(session.createQuery(criteriaQueryMock)).thenReturn(queryMock);
//        when(queryMock.getResultList()).thenReturn(expectedResult);
//
//        // Act
//        List<Training> result = trainingDAO.getTraineeTrainingList("Test.Test", 45);
//
//        // Verify
//        verify(session).createQuery(criteriaQueryCaptor.capture());
//        CriteriaQuery<Training> actualCriteriaQuery = criteriaQueryCaptor.getValue();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedResult, result);
//        assertEquals(criteriaQueryMock, actualCriteriaQuery);
//    }
//
//    @Test
//    void getTrainerTrainingListTest() {
//        // Arrange
//        HibernateCriteriaBuilder criteriaBuilderMock = mock(HibernateCriteriaBuilder.class);
//        JpaCriteriaQuery<Training> criteriaQueryMock = mock(JpaCriteriaQuery.class);
//        JpaRoot<Training> rootMock = mock(JpaRoot.class);
//        JpaPath<Object> trainerPathMock = mock(JpaPath.class);
//        JpaPath<Object> userPathMock = mock(JpaPath.class);
//        JpaPath<Object> usernamePathMock = mock(JpaPath.class);
//        Query<Training> queryMock = mock(Query.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//        List<Training> expectedResult = Collections.singletonList(training);
//
//        when(session.getCriteriaBuilder()).thenReturn(criteriaBuilderMock);
//        when(criteriaBuilderMock.createQuery(Training.class)).thenReturn(criteriaQueryMock);
//        when(criteriaQueryMock.from(Training.class)).thenReturn(rootMock);
//        when(rootMock.get("trainer")).thenReturn(trainerPathMock);
//        when(trainerPathMock.get("user")).thenReturn(userPathMock);
//        when(userPathMock.get("username")).thenReturn(usernamePathMock);
//        when(criteriaQueryMock.select(rootMock)).thenReturn(criteriaQueryMock);
//        when(criteriaQueryMock.where(predicates.toArray(new Predicate[]{}))).thenReturn(criteriaQueryMock);
//        when(session.createQuery(criteriaQueryMock)).thenReturn(queryMock);
//        when(queryMock.getResultList()).thenReturn(expectedResult);
//
//        // Act
//        List<Training> result = trainingDAO.getTrainerTrainingList("Test.Test", 45);
//
//        // Verify
//        verify(session).createQuery(criteriaQueryCaptor.capture());
//        CriteriaQuery<Training> actualCriteriaQuery = criteriaQueryCaptor.getValue();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(expectedResult, result);
//        assertEquals(criteriaQueryMock, actualCriteriaQuery);
//    }

    @Test
    void updateTraining() {
        // Arrange
        when(session.merge(training)).thenReturn(training);

        // Act
        Training updatedTraining = trainingDAO.updateTraining(training);

        // Assert
        assertEquals(training, updatedTraining);
        verify(session, times(1)).merge(training);
    }

    @Test
    void deleteTraining() {
        // Arrange
        when(session.merge(training)).thenReturn(training);
        doNothing().when(session).remove(any());

        // Act
        boolean result = trainingDAO.deleteTraining(training);

        // Assert
        assertTrue(result);
        verify(session, times(1)).merge(training);
        verify(session, times(1)).remove(training);
    }

    @Test
    void deleteTraining_NotFound() {
        // Arrange
        when(session.merge(any())).thenReturn(training);
        doThrow(new EntityNotFoundException("Entity not found")).when(session).remove(any());

        // Act
        boolean result = trainingDAO.deleteTraining(training);

        // Assert
        assertFalse(result);
        verify(session, times(1)).merge(training);
        verify(session, times(1)).remove(training);
    }

    @Test
    void getAllTrainings() {
        // Arrange
        Query<Training> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(training));

        // Act
        List<Training> trainings = trainingDAO.findAllTrainings();

        // Assert
        assertEquals(1, trainings.size());
        assertEquals(training, trainings.get(0));
    }

}

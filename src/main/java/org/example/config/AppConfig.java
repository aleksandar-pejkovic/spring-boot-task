package org.example.config;

import org.example.controller.LoginController;
import org.example.controller.TraineeController;
import org.example.controller.TrainerController;
import org.example.controller.TrainingController;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dao.UserDAO;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.utils.CredentialsGenerator;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig {

    @Bean
    public CredentialsGenerator credentialsGenerator(SessionFactory sessionFactory) {
        return new CredentialsGenerator(sessionFactory);
    }

    @Bean
    public TraineeDAO traineeDAO(SessionFactory sessionFactory) {
        return new TraineeDAO(sessionFactory);
    }

    @Bean
    public TrainerDAO trainerDAO(SessionFactory sessionFactory) {
        return new TrainerDAO(sessionFactory);
    }

    @Bean
    public TrainingDAO trainingDAO(SessionFactory sessionFactory) {
        return new TrainingDAO(sessionFactory);
    }

    @Bean
    public UserDAO userDAO(SessionFactory sessionFactory) {
        return new UserDAO(sessionFactory);
    }

    @Bean
    public TraineeService traineeService(TraineeDAO traineeDAO, CredentialsGenerator credentialsGenerator) {
        return new TraineeService(traineeDAO, credentialsGenerator);
    }

    @Bean
    public TrainerService trainerService(TrainerDAO trainerDAO, TraineeDAO traineeDAO,
                                         CredentialsGenerator credentialsGenerator, TrainingDAO trainingDAO) {
        return new TrainerService(trainerDAO, traineeDAO, credentialsGenerator, trainingDAO);
    }

    @Bean
    public TrainingService trainingService(TrainingDAO trainingDAO,
                                           TraineeDAO traineeDAO,
                                           TrainerDAO trainerDAO
    ) {
        return new TrainingService(trainingDAO, traineeDAO, trainerDAO);
    }

    @Bean
    public TraineeController traineeController(TraineeService traineeService) {
        return new TraineeController(traineeService);
    }

    @Bean
    public TrainerController trainerController(TrainerService trainerService) {
        return new TrainerController(trainerService);
    }

    @Bean
    public TrainingController trainingController(TrainingService trainingService) {
        return new TrainingController(trainingService);
    }

    @Bean
    public LoginController loginController(AuthenticationManager authenticationManager) {
        return new LoginController(authenticationManager);
    }
}

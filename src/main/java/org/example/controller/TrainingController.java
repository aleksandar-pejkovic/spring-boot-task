package org.example.controller;

import java.util.Date;
import java.util.List;

import org.example.dto.training.TrainingCreateDTO;
import org.example.dto.training.TrainingDTO;
import org.example.dto.trainingType.TrainingTypeDTO;
import org.example.enums.TrainingTypeName;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.service.TrainingService;
import org.example.utils.converter.TrainingConverter;
import org.example.utils.converter.TrainingTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/trainee")
    public List<TrainingDTO> getTraineeTrainingsList(
            @RequestParam String username,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) TrainingTypeName trainingType
    ) {
        log.info("Endpoint '/api/trainings/trainee' was called to get a trainee's training list");
        List<Training> trainings = trainingService.getTraineeTrainingList(
                username,
                periodFrom,
                periodTo,
                trainerName,
                trainingType.name());
        return TrainingConverter.convertToDtoList(trainings);
    }

    @GetMapping("/trainer")
    public List<TrainingDTO> getTrainerTrainingsList(
            @RequestParam String username,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        log.info("Endpoint '/api/trainings/trainer' was called to get a trainer's training list");
        List<Training> trainings = trainingService.getTrainerTrainingList(
                username,
                periodFrom,
                periodTo,
                traineeName);
        return TrainingConverter.convertToDtoList(trainings);
    }

    @PostMapping
    public ResponseEntity<Boolean> addTraining(@Valid @RequestBody TrainingCreateDTO trainingCreateDTO) {
        log.info("Endpoint '/api/trainings' was called to add new training");
        boolean successfullyAddedTraining = trainingService.createTraining(trainingCreateDTO);
        return (successfullyAddedTraining)
                ? ResponseEntity.ok(true)
                : ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/training-types")
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        log.info("Endpoint '/api/trainings/training-types' was called to get all training types");
        List<TrainingType> trainingTypes = trainingService.finaAllTrainingTypes();
        return TrainingTypeConverter.convertToDtoList(trainingTypes);
    }
}

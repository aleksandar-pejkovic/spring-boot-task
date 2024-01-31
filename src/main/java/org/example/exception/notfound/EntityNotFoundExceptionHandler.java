package org.example.exception.notfound;

import static org.example.utils.exception.ErrorResponseFactory.getErrorResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EntityNotFoundExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TraineeNotFoundException.class)
    public Map<String, String> handleTraineeNotFoundException(TraineeNotFoundException ex) {
        return getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainerNotFoundException.class)
    public Map<String, String> handleTrainerNotFoundException(TrainerNotFoundException ex) {
        return getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingNotFoundException.class)
    public Map<String, String> handleTrainingNotFoundException(TrainingNotFoundException ex) {
        return getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingTypeNotFoundException.class)
    public Map<String, String> handleTrainingNotFoundException(TrainingTypeNotFoundException ex) {
        return getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException ex) {
        return getErrorResponse(ex.getMessage());
    }
}

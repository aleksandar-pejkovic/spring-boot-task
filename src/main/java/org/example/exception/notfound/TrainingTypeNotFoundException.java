package org.example.exception.notfound;

public class TrainingTypeNotFoundException extends RuntimeException {

    public TrainingTypeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

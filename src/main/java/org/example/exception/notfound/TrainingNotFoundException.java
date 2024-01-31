package org.example.exception.notfound;

public class TrainingNotFoundException extends RuntimeException {

    public TrainingNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

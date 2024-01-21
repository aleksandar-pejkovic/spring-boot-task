package org.example.exception.notfound;

public class TraineeNotFoundException extends RuntimeException {

    public TraineeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

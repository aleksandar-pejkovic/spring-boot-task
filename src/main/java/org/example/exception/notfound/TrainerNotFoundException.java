package org.example.exception.notfound;

public class TrainerNotFoundException extends RuntimeException {

    public TrainerNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}

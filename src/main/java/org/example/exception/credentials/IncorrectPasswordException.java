package org.example.exception.credentials;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException(String errorMessage) {
        super(errorMessage);
    }
}

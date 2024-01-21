package org.example.exception.credentials;

public class IdenticalPasswordException extends RuntimeException {

    public IdenticalPasswordException(String errorMessage) {
        super(errorMessage);
    }
}

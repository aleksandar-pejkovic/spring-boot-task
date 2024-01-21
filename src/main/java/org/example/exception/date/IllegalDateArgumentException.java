package org.example.exception.date;

public class IllegalDateArgumentException extends RuntimeException {

    public IllegalDateArgumentException(String errorMessage) {
        super(errorMessage);
    }
}

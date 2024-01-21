package org.example.exception.credentials;

import static org.example.utils.exception.ErrorResponseFactory.getErrorResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CredentialsExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IdenticalPasswordException.class)
    public Map<String, String> handleIdenticalPasswordException(IdenticalPasswordException ex) {
        return getErrorResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectPasswordException.class)
    public Map<String, String> handleIncorrectPasswordException(IncorrectPasswordException ex) {
        return getErrorResponse(ex.getMessage());
    }
}

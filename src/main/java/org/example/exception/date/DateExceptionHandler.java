package org.example.exception.date;

import static org.example.utils.exception.ErrorResponseFactory.getErrorResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DateExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalDateArgumentException.class)
    public Map<String, String> handleIllegalDateArgumentException(IllegalDateArgumentException ex) {
        return getErrorResponse(ex.getMessage());
    }
}

package org.example.utils.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static Map<String, String> getErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }
}

package br.com.customer.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class WorkoutNotFoundException extends APIException {
    public WorkoutNotFoundException() { super(HttpStatus.NOT_FOUND, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND.value())
                .detail("Workout not found.")
                .build();
    }
}

package br.com.customer.exception;

import org.springframework.http.HttpStatus;

public class ExerciseNotFoundException extends APIException {
    public ExerciseNotFoundException() { super(HttpStatus.NOT_FOUND, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND.value())
                .detail("Exercise not found.")
                .build();
    }
}

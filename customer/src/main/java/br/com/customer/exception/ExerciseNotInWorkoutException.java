package br.com.customer.exception;

import org.springframework.http.HttpStatus;

public class ExerciseNotInWorkoutException extends APIException {
    public ExerciseNotInWorkoutException() { super(HttpStatus.NOT_FOUND, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND.value())
                .detail("Exercise is not part of this workout.")
                .build();
    }
}

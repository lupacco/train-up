package br.com.customer.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class UserNotFoundException extends APIException {
    public UserNotFoundException() { super(HttpStatus.NOT_FOUND, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND.value())
                .detail("User not found.")
                .build();
    }
}

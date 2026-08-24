package br.com.customer.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends APIException {
    public ForbiddenException() { super(HttpStatus.FORBIDDEN, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.FORBIDDEN.getReasonPhrase())
                .status(HttpStatus.FORBIDDEN.value())
                .detail("This resource does not belong to you.")
                .build();
    }
}

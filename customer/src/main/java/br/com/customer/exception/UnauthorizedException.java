package br.com.customer.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends APIException {
    public UnauthorizedException() { super(HttpStatus.UNAUTHORIZED, getErrorResponse()); }

    private static ExceptionDetails getErrorResponse(){
        return ExceptionDetails.builder()
                .title(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail("Authentication required.")
                .build();
    }
}

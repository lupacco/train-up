package br.com.customer.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class APIException extends RuntimeException{

    private final HttpStatus status;
    private final ExceptionDetails exceptionDetails;

    public APIException(HttpStatus status, ExceptionDetails exceptionDetails, String message) {
        super(message);
        this.status = status;
        this.exceptionDetails = exceptionDetails;
    }

    public APIException(HttpStatus status, ExceptionDetails exceptionDetails) {
        this.status = status;
        this.exceptionDetails = exceptionDetails;
    }

    public ResponseEntity<ExceptionDetails> buildErrorResponse(HttpServletRequest request){
        this.exceptionDetails.addRequest(request);
        return ResponseEntity.status(status).body(exceptionDetails);
    }

}

package br.com.customer.handler;

import br.com.customer.exception.ConflictException;
import br.com.customer.exception.ExceptionDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDetails> handleConflictException(HttpServletRequest request, ConflictException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ExceptionDetails.builder()
                        .title(HttpStatus.CONFLICT.getReasonPhrase())
                        .status(HttpStatus.CONFLICT.value())
                        .detail(exception.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

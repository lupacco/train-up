package br.com.customer.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class ExceptionDetails {
    protected String title;
    protected Integer status;
    protected String detail;
    protected String path;
    protected LocalDateTime timestamp;

    public void addRequest(HttpServletRequest request){
        this.path = request.getRequestURI();
        this.timestamp = LocalDateTime.now();
    }
}

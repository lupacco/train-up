package br.com.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class ExceptionDetails {
    protected String title;
    protected Integer status;
    protected String detail;
    protected String path;
    protected LocalDateTime timestamp;
}

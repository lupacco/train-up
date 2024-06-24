package br.com.core.request;

import java.time.LocalDate;
import java.util.Date;

public record RegisterPostRequest(
        String name,
        String username,
        String email,
        String password,
        LocalDate birthdate
) {
}

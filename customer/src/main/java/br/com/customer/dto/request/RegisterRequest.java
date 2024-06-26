package br.com.customer.dto.request;

import java.time.LocalDate;

public record RegisterRequest(
        String name,
        String username,
        String email,
        String password,
        LocalDate birthdate
) {
}

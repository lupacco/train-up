package br.com.customer.dto.request;

import java.time.LocalDate;

public record AuthenticationRequest(
        String username,
        String password
) {
}

package br.com.customer.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CustomerUserGetResponse(
        UUID id,
        String name,
        String username,
        String email,
        LocalDate birthdate
) {
}

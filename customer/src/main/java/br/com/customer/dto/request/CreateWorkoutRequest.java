package br.com.customer.dto.request;

import java.util.UUID;

public record CreateWorkoutRequest(
        String name,
        UUID iconId,
        UUID userId
) {
}

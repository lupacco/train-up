package br.com.customer.dto.response;

import br.com.customer.model.Workout;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WorkoutGetResponse(
        UUID id,
        String name,
        UUID iconId,
        CustomerUserGetResponse createdByUser
) {
}

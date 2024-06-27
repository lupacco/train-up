package br.com.customer.dto.response;

import br.com.customer.model.Workout;
import lombok.Builder;

@Builder
public record WorkoutGetResponse(
        Workout workout
) {
}

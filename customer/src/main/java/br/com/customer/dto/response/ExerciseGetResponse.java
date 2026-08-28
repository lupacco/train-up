package br.com.customer.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ExerciseGetResponse(
        UUID exerciseId,
        String name,
        Short series,
        List<Short> repsGoals,
        List<Float> weightGoals
) {
}

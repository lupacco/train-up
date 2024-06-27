package br.com.customer.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ExerciseGetResponse(
        String name,
        Short series,
        List<Short> repsGoals,
        List<Float> weightGoals
) {
}

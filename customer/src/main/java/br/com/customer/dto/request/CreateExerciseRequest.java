package br.com.customer.dto.request;

import java.util.List;

public record CreateExerciseRequest(
        String name,
        Short series,
        List<Short> repsGoals,
        List<Float> weightGoals
) {
}

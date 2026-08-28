package br.com.customer.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ExercisePerformedGetResponse(
        UUID exerciseId,
        UUID workoutPerformedId,
        Short serie,
        Short repsGoal,
        Short repsPerformed,
        Float weightGoal,
        Float weightPerformed
) {
}

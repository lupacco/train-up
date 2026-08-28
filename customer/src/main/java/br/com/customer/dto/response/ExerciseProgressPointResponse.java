package br.com.customer.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ExerciseProgressPointResponse(
        UUID workoutPerformedId,
        LocalDateTime performedAt,
        List<ExercisePerformedGetResponse> series,
        Integer totalReps,
        Float maxWeight
) {
}

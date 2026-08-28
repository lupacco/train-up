package br.com.customer.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record WorkoutPerformedGetResponse(
        UUID id,
        UUID workoutId,
        String workoutName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}

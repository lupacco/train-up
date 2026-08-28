package br.com.customer.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record WorkoutPerformedDetailResponse(
        UUID id,
        UUID workoutId,
        String workoutName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<ExerciseGetResponse> exercises,
        List<ExercisePerformedGetResponse> performed
) {
}

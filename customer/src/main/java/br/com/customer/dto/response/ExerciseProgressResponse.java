package br.com.customer.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ExerciseProgressResponse(
        UUID exerciseId,
        String exerciseName,
        List<ExerciseProgressPointResponse> points
) {
}

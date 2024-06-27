package br.com.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutExerciseId {
    @Column(name = "workout_id", nullable = false)
    UUID workoutId;

    @Column(name = "exercise_id", nullable = false)
    UUID exerciseId;
}
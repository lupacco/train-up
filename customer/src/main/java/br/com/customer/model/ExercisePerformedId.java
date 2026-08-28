package br.com.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExercisePerformedId implements Serializable {

    @Column(name = "exercise_id", nullable = false)
    UUID exerciseId;

    @Column(name = "workout_performed_id", nullable = false)
    UUID workoutPerformedId;

    @Column(name = "serie", nullable = false)
    Short serie;
}

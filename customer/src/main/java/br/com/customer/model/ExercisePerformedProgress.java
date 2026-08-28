package br.com.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Flat row of one logged serie joined with the session it belongs to. */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExercisePerformedProgress {

    private UUID exerciseId;

    private UUID workoutPerformedId;

    private LocalDateTime performedAt;

    private Short serie;

    private Short repsGoal;

    private Short repsPerformed;

    private Float weightGoal;

    private Float weightPerformed;
}

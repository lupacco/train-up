package br.com.customer.model;

import br.com.customer.dto.response.ExercisePerformedGetResponse;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "exercise_performed")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExercisePerformed {

    @EmbeddedId
    private ExercisePerformedId id;

    /** Goal copied from workout_exercise when the serie is logged, so history
     * keeps the target the user was chasing at that moment. */
    @Column(name = "reps_goal")
    private Short repsGoal;

    @Column(name = "reps_performed")
    private Short repsPerformed;

    /** BigDecimal because the columns are numeric(5,1); Float fails ddl validation. */
    @Column(name = "weight_goal")
    private BigDecimal weightGoal;

    /** The column name carries a typo from migration V4 (weigth_performed). */
    @Column(name = "weigth_performed")
    private BigDecimal weightPerformed;

    public ExercisePerformedGetResponse toGetResponse(){
        return ExercisePerformedGetResponse.builder()
                .exerciseId(this.id.getExerciseId())
                .workoutPerformedId(this.id.getWorkoutPerformedId())
                .serie(this.id.getSerie())
                .repsGoal(this.repsGoal)
                .repsPerformed(this.repsPerformed)
                .weightGoal(this.weightGoal == null ? null : this.weightGoal.floatValue())
                .weightPerformed(this.weightPerformed == null ? null : this.weightPerformed.floatValue())
                .build();
    }
}

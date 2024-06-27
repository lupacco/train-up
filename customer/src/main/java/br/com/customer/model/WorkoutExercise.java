package br.com.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "workout_exercise")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkoutExercise {

    @EmbeddedId
    private WorkoutExerciseId id;

    @Column
    Short series;

    @Column(name = "rep_goals")
    List<Short> repsGoals;

    @Column(name = "weight_goals")
    List<Float> weightGoals;
}



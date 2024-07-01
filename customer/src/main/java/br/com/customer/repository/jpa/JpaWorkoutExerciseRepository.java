package br.com.customer.repository.jpa;

import br.com.customer.model.WorkoutExercise;
import br.com.customer.model.WorkoutExerciseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaWorkoutExerciseRepository extends JpaRepository<WorkoutExercise, WorkoutExerciseId> {

    @Query(value = "select * from workout_exercise where workout_id = :workoutId and exercise_id = :exerciseId", nativeQuery = true)
    WorkoutExercise findWorkoutExerciseRelation(@Param("workoutId") UUID workoutId, @Param("exerciseId") UUID exerciseId);
}

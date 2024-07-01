package br.com.customer.repository.jpa;

import br.com.customer.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaExerciseRepository extends JpaRepository<Exercise, UUID> {

    @Query(value = """
            select e.* from exercise e
               join workout_exercise we on we.exercise_id = e.id
               join workout w on w.id = we.workout_id
               where w.id = :workoutId
            """, nativeQuery = true)
    List<Exercise> findAllExercisesFromWorkout(@Param("workoutId")UUID workoutId);

}

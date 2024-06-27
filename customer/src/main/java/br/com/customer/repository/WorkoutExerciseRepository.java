package br.com.customer.repository;

import br.com.customer.model.WorkoutExercise;
import br.com.customer.model.WorkoutExerciseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, WorkoutExerciseId> {
}

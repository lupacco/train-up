package br.com.customer.repository.jpa;

import br.com.customer.model.ExercisePerformed;
import br.com.customer.model.ExercisePerformedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaExercisePerformedRepository extends JpaRepository<ExercisePerformed, ExercisePerformedId> {

    @Query(value = """
            select ep.* from exercise_performed ep
            where ep.workout_performed_id = :performedId
            order by ep.exercise_id, ep.serie
            """, nativeQuery = true)
    List<ExercisePerformed> findAllBySession(@Param("performedId") UUID performedId);
}

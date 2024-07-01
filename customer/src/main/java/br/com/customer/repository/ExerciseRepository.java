package br.com.customer.repository;

import br.com.customer.model.ExerciseWorkoutGoals;
import br.com.customer.util.ExerciseWorkoutGoalsRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExerciseRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<ExerciseWorkoutGoals> listAllWorkoutExercisesWithGoals(UUID workoutId){
        log.debug("[start] ExerciseRepository - listAllWorkoutExercisesWithGoals");
        String sql = """
                select e.*, we.series, we.rep_goals, we.weight_goals from exercise e
                   join workout_exercise we on we.exercise_id = e.id
                   join workout w on w.id = we.workout_id
                   where w.id = ?
                """;
        var result = jdbcTemplate.query(sql, new ExerciseWorkoutGoalsRowMapper(), workoutId);
        log.debug("[finish] ExerciseRepository - listAllWorkoutExercisesWithGoals");
        return result;
    }
}

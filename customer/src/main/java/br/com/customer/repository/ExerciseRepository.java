package br.com.customer.repository;

import br.com.customer.dto.response.ExerciseGetResponse;
import br.com.customer.model.Exercise;
import br.com.customer.model.ExerciseWorkoutGoals;
import br.com.customer.model.WorkoutExercise;
import br.com.customer.repository.jpa.JpaExerciseRepository;
import br.com.customer.repository.jpa.JpaWorkoutExerciseRepository;
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
    private final JpaExerciseRepository jpaExerciseRepository;
    private final JpaWorkoutExerciseRepository jpaWorkoutExerciseRepository;

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

    public List<ExerciseGetResponse> listAllWorkoutExercisesWithGoalsJpa(UUID workoutId){
        log.debug("[start] ExerciseRepository - listAllWorkoutExercisesWithGoalsJpa");
        List<Exercise> exercises = jpaExerciseRepository.findAllExercisesFromWorkout(workoutId);
        List<ExerciseGetResponse> result = exercises.stream()
                .map(exercise -> {
                        WorkoutExercise relation = jpaWorkoutExerciseRepository.findWorkoutExerciseRelation(workoutId, exercise.getId());
                        return ExerciseGetResponse.builder()
                        .name(exercise.getName())
                        .series(relation.getSeries())
                        .repsGoals(relation.getRepGoals())
                        .weightGoals(relation.getWeightGoals())
                        .build();
                })
                .toList();
        log.debug("[finish] ExerciseRepository - listAllWorkoutExercisesWithGoalsJpa");
        return result;
    }

    public Exercise save(Exercise exercise){
        return jpaExerciseRepository.save(exercise);
    }
}

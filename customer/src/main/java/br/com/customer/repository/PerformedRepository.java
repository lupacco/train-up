package br.com.customer.repository;

import br.com.customer.model.ExercisePerformedProgress;
import br.com.customer.util.ExercisePerformedProgressRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PerformedRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Every serie the given user logged for one exercise, oldest first, so the
     * service can fold it into the progression chart. Filtering by
     * performed_by_user is what keeps the shared exercise catalog from leaking
     * other people's numbers.
     */
    public List<ExercisePerformedProgress> listProgress(UUID exerciseId, UUID customerId){
        log.debug("[start] PerformedRepository - listProgress");
        String sql = """
                select ep.exercise_id, ep.workout_performed_id, wp.start_time, ep.serie,
                       ep.reps_goal, ep.reps_performed, ep.weight_goal, ep.weigth_performed
                from exercise_performed ep
                    join workout_performed wp on wp.id = ep.workout_performed_id
                where ep.exercise_id = ? and wp.performed_by_user = ?
                order by wp.start_time, ep.serie
                """;
        var result = jdbcTemplate.query(sql, new ExercisePerformedProgressRowMapper(), exerciseId, customerId);
        log.debug("[finish] PerformedRepository - listProgress");
        return result;
    }
}

package br.com.customer.util;

import br.com.customer.model.ExercisePerformedProgress;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ExercisePerformedProgressRowMapper implements RowMapper<ExercisePerformedProgress> {

    @Override
    public ExercisePerformedProgress mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExercisePerformedProgress row = new ExercisePerformedProgress();

        row.setExerciseId(UUID.fromString(rs.getString("exercise_id")));
        row.setWorkoutPerformedId(UUID.fromString(rs.getString("workout_performed_id")));
        row.setPerformedAt(rs.getTimestamp("start_time") != null
                ? rs.getTimestamp("start_time").toLocalDateTime()
                : null);
        row.setSerie(rs.getShort("serie"));
        row.setRepsGoal(readShort(rs, "reps_goal"));
        row.setRepsPerformed(readShort(rs, "reps_performed"));
        row.setWeightGoal(readFloat(rs, "weight_goal"));
        row.setWeightPerformed(readFloat(rs, "weigth_performed"));

        return row;
    }

    private Short readShort(ResultSet rs, String column) throws SQLException {
        short value = rs.getShort(column);
        return rs.wasNull() ? null : value;
    }

    private Float readFloat(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return value == null ? null : value.floatValue();
    }
}

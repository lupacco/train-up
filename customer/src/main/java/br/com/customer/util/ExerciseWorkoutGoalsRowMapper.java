package br.com.customer.util;

import br.com.customer.model.ExerciseWorkoutGoals;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class ExerciseWorkoutGoalsRowMapper implements RowMapper<ExerciseWorkoutGoals> {

    @Override
    public ExerciseWorkoutGoals mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExerciseWorkoutGoals exerciseWorkoutGoals = new ExerciseWorkoutGoals();

        exerciseWorkoutGoals.setId(UUID.fromString(rs.getString("id")));
        exerciseWorkoutGoals.setName(rs.getString("name"));
        exerciseWorkoutGoals.setSeries(rs.getShort("series"));
        exerciseWorkoutGoals.setRepGoals(Arrays.stream((Short[]) rs.getArray("rep_goals").getArray()).toList());

        BigDecimal[] weightGoalsArray = (BigDecimal[]) rs.getArray("weight_goals").getArray();
        exerciseWorkoutGoals.setWeightGoals(Arrays.stream(weightGoalsArray)
                .map(BigDecimal::floatValue)
                .toList());

        exerciseWorkoutGoals.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        exerciseWorkoutGoals.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        exerciseWorkoutGoals.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);

        return exerciseWorkoutGoals;
    }
}

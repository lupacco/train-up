package br.com.customer;

import br.com.customer.dto.request.CreateExerciseRequest;
import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.request.LogSerieRequest;
import br.com.customer.dto.request.StartSessionRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkoutSessionFlowTest extends IntegrationTestSupport {

    private static final UUID SEEDED_ICON = UUID.fromString("11111111-1111-4111-8111-000000000001");

    @Test
    @DisplayName("logs a whole session and folds it into the exercise progression")
    void logsSessionAndBuildsProgress() throws Exception {
        String token = authenticate("flowuser");

        UUID workoutId = createWorkout(token, "Biceps");
        JsonNode exercises = addExercises(token, workoutId, new CreateExerciseRequest(
                "Rosca direta",
                (short) 2,
                List.of((short) 12, (short) 10),
                List.of(20.0f, 25.0f)
        ));

        UUID exerciseId = UUID.fromString(exercises.get(0).get("exerciseId").asText());

        UUID performedId = startSession(token, workoutId);

        // Starting again resumes the open session instead of duplicating it.
        assertThat(startSession(token, workoutId)).isEqualTo(performedId);

        // The goal is copied from workout_exercise into the logged serie.
        mockMvc.perform(put("/api/v1/performed/{p}/exercise/{e}/serie/{s}", performedId, exerciseId, 1)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LogSerieRequest((short) 12, 20.0f))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repsGoal").value(12))
                .andExpect(jsonPath("$.weightGoal").value(20.0))
                .andExpect(jsonPath("$.repsPerformed").value(12))
                .andExpect(jsonPath("$.weightPerformed").value(20.0));

        logSerie(token, performedId, exerciseId, 2, (short) 10, 25.0f);

        // Re-sending the same serie is an upsert, not a duplicate row.
        logSerie(token, performedId, exerciseId, 2, (short) 9, 25.0f);

        mockMvc.perform(patch("/api/v1/performed/{p}/finish", performedId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endTime").isNotEmpty());

        mockMvc.perform(patch("/api/v1/performed/{p}/finish", performedId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].workoutName").value("Biceps"));

        mockMvc.perform(get("/api/v1/performed/{p}", performedId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises.length()").value(1))
                .andExpect(jsonPath("$.performed.length()").value(2));

        mockMvc.perform(get("/api/v1/performed")
                        .param("date", LocalDate.now().toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/performed")
                        .param("year", String.valueOf(LocalDate.now().getYear()))
                        .param("month", String.valueOf(LocalDate.now().getMonthValue()))
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 12 + 9 reps on a single session, heaviest serie at 25kg.
        mockMvc.perform(get("/api/v1/exercise/{e}/progress", exerciseId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value("Rosca direta"))
                .andExpect(jsonPath("$.points.length()").value(1))
                .andExpect(jsonPath("$.points[0].totalReps").value(21))
                .andExpect(jsonPath("$.points[0].maxWeight").value(25.0))
                .andExpect(jsonPath("$.points[0].series.length()").value(2));
    }

    @Test
    @DisplayName("logs a session on a past day without touching the live open run")
    void logsBackdatedSession() throws Exception {
        String token = authenticate("diary");
        UUID workoutId = createWorkout(token, "Pull");
        LocalDate yesterday = LocalDate.now().minusDays(1);

        UUID liveId = startSession(token, workoutId);
        UUID diaryId = startSessionOn(token, workoutId, yesterday);

        assertThat(diaryId).isNotEqualTo(liveId);

        mockMvc.perform(patch("/api/v1/performed/{p}/finish", diaryId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value(org.hamcrest.Matchers.startsWith(yesterday.toString())))
                .andExpect(jsonPath("$.endTime").value(org.hamcrest.Matchers.startsWith(yesterday.toString())));

        mockMvc.perform(get("/api/v1/performed")
                        .param("date", yesterday.toString())
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(post("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new StartSessionRequest(LocalDate.now().plusDays(1)))))
                .andExpect(status().isConflict());
    }

    @Test
    void reusesExerciseCatalog() throws Exception {
        String token = authenticate("catalog");

        UUID pushDay = createWorkout(token, "Push");
        UUID pullDay = createWorkout(token, "Pull");

        CreateExerciseRequest request = new CreateExerciseRequest(
                "Remada curvada",
                (short) 1,
                List.of((short) 10),
                List.of(40.0f)
        );

        UUID first = UUID.fromString(
                addExercises(token, pushDay, request).get(0).get("exerciseId").asText());
        UUID second = UUID.fromString(
                addExercises(token, pullDay, request).get(0).get("exerciseId").asText());

        assertThat(second).isEqualTo(first);
    }

    @Test
    @DisplayName("refuses to touch a workout that belongs to somebody else")
    void refusesForeignWorkout() throws Exception {
        String owner = authenticate("owner");
        String intruder = authenticate("intruder");

        UUID workoutId = createWorkout(owner, "Legs");

        mockMvc.perform(get("/api/v1/workout/{w}/exercises", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(intruder)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(intruder)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(intruder)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("lists only the workouts of the caller")
    void listsOnlyMyWorkouts() throws Exception {
        String mine = authenticate("mine");
        String theirs = authenticate("theirs");

        createWorkout(mine, "Mine only");
        createWorkout(theirs, "Theirs only");

        mockMvc.perform(get("/api/v1/workout/me")
                        .header(HttpHeaders.AUTHORIZATION, bearer(mine)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Mine only"));
    }

    @Test
    @DisplayName("requires a token on workout endpoints")
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/workout/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("serves the seeded workout icons")
    void servesSeededIcons() throws Exception {
        String token = authenticate("icons");

        mockMvc.perform(get("/api/v1/icon")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[?(@.name == 'barbell-orange')]").exists());
    }

    private UUID createWorkout(String token, String name) throws Exception {
        String body = mockMvc.perform(post("/api/v1/workout")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CreateWorkoutRequest(name, SEEDED_ICON))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    private JsonNode addExercises(String token, UUID workoutId, CreateExerciseRequest... exercises)
            throws Exception {
        String body = mockMvc.perform(post("/api/v1/workout/{w}/exercise", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(List.of(exercises))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body);
    }

    private UUID startSession(String token, UUID workoutId) throws Exception {
        String body = mockMvc.perform(post("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    private UUID startSessionOn(String token, UUID workoutId, LocalDate day) throws Exception {
        String body = mockMvc.perform(post("/api/v1/workout/{w}/performed", workoutId)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new StartSessionRequest(day))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(body).get("id").asText());
    }

    private void logSerie(String token, UUID performedId, UUID exerciseId, int serie,
                          Short reps, Float weight) throws Exception {
        mockMvc.perform(put("/api/v1/performed/{p}/exercise/{e}/serie/{s}", performedId, exerciseId, serie)
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LogSerieRequest(reps, weight))))
                .andExpect(status().isOk());
    }
}

package br.com.customer.service;

import br.com.customer.dto.request.LogSerieRequest;
import br.com.customer.dto.response.*;
import br.com.customer.exception.ConflictException;
import br.com.customer.exception.ExerciseNotInWorkoutException;
import br.com.customer.exception.ForbiddenException;
import br.com.customer.exception.WorkoutPerformedNotFoundException;
import br.com.customer.model.*;
import br.com.customer.repository.ExerciseRepository;
import br.com.customer.repository.PerformedRepository;
import br.com.customer.repository.jpa.JpaExercisePerformedRepository;
import br.com.customer.repository.jpa.JpaWorkoutExerciseRepository;
import br.com.customer.repository.jpa.JpaWorkoutPerformedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Owns the execution side of the domain: workout_performed (one run of a workout)
 * and exercise_performed (one logged serie). Goals stay on workout_exercise and
 * are copied into each logged serie so history keeps the target of that moment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSessionService {

    private final JpaWorkoutPerformedRepository jpaWorkoutPerformedRepository;
    private final JpaExercisePerformedRepository jpaExercisePerformedRepository;
    private final JpaWorkoutExerciseRepository jpaWorkoutExerciseRepository;
    private final PerformedRepository performedRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutService workoutService;
    private final CurrentUserService currentUserService;

    @Transactional
    public WorkoutPerformedGetResponse start(UUID workoutId, LocalDate performedOn){
        log.debug("[start] WorkoutSessionService - start");
        Workout workout = workoutService.findOwnedById(workoutId);
        CustomerUser customerUser = currentUserService.require();
        LocalDate today = LocalDate.now();

        if (performedOn != null && performedOn.isAfter(today)) {
            throw new ConflictException("Cannot log a workout in the future.");
        }

        // A past date is a diary entry: never reuse the live open session,
        // otherwise "I forgot yesterday" would jump into today's gym run.
        boolean live = performedOn == null || performedOn.equals(today);
        WorkoutPerformed session;
        if (live) {
            session = jpaWorkoutPerformedRepository
                    .findOpenSession(workout.getId(), customerUser.getId())
                    .orElseGet(() -> jpaWorkoutPerformedRepository.save(WorkoutPerformed.builder()
                            .workout(workout)
                            .performedByUser(customerUser)
                            .startTime(LocalDateTime.now())
                            .build()));
        } else {
            session = jpaWorkoutPerformedRepository.save(WorkoutPerformed.builder()
                    .workout(workout)
                    .performedByUser(customerUser)
                    .startTime(performedOn.atTime(12, 0))
                    .build());
        }

        log.debug("[finish] WorkoutSessionService - start");
        return session.toGetResponse();
    }

    @Transactional
    public WorkoutPerformedGetResponse finish(UUID performedId){
        log.debug("[start] WorkoutSessionService - finish");
        WorkoutPerformed session = findOwnedSession(performedId);

        if (session.getEndTime() != null) throw new ConflictException("Session already finished.");

        // Backfilled sessions must stay on their logged day, or duration and
        // the calendar would leak into "now".
        LocalDate startedOn = session.getStartTime().toLocalDate();
        session.setEndTime(startedOn.equals(LocalDate.now())
                ? LocalDateTime.now()
                : session.getStartTime().plusMinutes(1));
        WorkoutPerformed result = jpaWorkoutPerformedRepository.save(session);
        log.debug("[finish] WorkoutSessionService - finish");
        return result.toGetResponse();
    }

    @Transactional
    public ExercisePerformedGetResponse logSerie(UUID performedId, UUID exerciseId, Short serie, LogSerieRequest request){
        log.debug("[start] WorkoutSessionService - logSerie");
        WorkoutPerformed session = findOwnedSession(performedId);

        WorkoutExercise relation = jpaWorkoutExerciseRepository
                .findWorkoutExerciseRelation(session.getWorkout().getId(), exerciseId);
        if (relation == null) throw new ExerciseNotInWorkoutException();

        if (serie == null || serie < 1) throw new ConflictException("Serie must start at 1.");
        if (relation.getSeries() != null && serie > relation.getSeries()) {
            throw new ConflictException("This workout plans only " + relation.getSeries() + " series.");
        }

        ExercisePerformed performed = ExercisePerformed.builder()
                .id(new ExercisePerformedId(exerciseId, session.getId(), serie))
                .repsGoal(goalAt(relation.getRepGoals(), serie))
                .weightGoal(toDecimal(goalAt(relation.getWeightGoals(), serie)))
                .repsPerformed(request.repsPerformed())
                .weightPerformed(toDecimal(request.weightPerformed()))
                .build();

        // save() on an existing composite id merges, which makes the endpoint an upsert.
        ExercisePerformed result = jpaExercisePerformedRepository.save(performed);
        log.debug("[finish] WorkoutSessionService - logSerie");
        return result.toGetResponse();
    }

    public WorkoutPerformedDetailResponse detail(UUID performedId){
        log.debug("[start] WorkoutSessionService - detail");
        WorkoutPerformed session = findOwnedSession(performedId);

        List<ExerciseGetResponse> exercises = workoutService
                .listAllWorkoutExercises(session.getWorkout().getId());
        List<ExercisePerformedGetResponse> performed = jpaExercisePerformedRepository
                .findAllBySession(session.getId()).stream()
                .map(ExercisePerformed::toGetResponse)
                .toList();

        log.debug("[finish] WorkoutSessionService - detail");
        return WorkoutPerformedDetailResponse.builder()
                .id(session.getId())
                .workoutId(session.getWorkout().getId())
                .workoutName(session.getWorkout().getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .exercises(exercises)
                .performed(performed)
                .build();
    }

    public List<WorkoutPerformedGetResponse> history(UUID workoutId){
        log.debug("[start] WorkoutSessionService - history");
        Workout workout = workoutService.findOwnedById(workoutId);
        CustomerUser customerUser = currentUserService.require();
        var result = jpaWorkoutPerformedRepository
                .findAllByWorkoutAndUser(workout.getId(), customerUser.getId()).stream()
                .map(WorkoutPerformed::toGetResponse)
                .toList();
        log.debug("[finish] WorkoutSessionService - history");
        return result;
    }

    public List<WorkoutPerformedGetResponse> listByDay(LocalDate day){
        log.debug("[start] WorkoutSessionService - listByDay");
        CustomerUser customerUser = currentUserService.require();
        var result = jpaWorkoutPerformedRepository.findAllByDay(customerUser.getId(), day).stream()
                .map(WorkoutPerformed::toGetResponse)
                .toList();
        log.debug("[finish] WorkoutSessionService - listByDay");
        return result;
    }

    public List<WorkoutPerformedGetResponse> listByMonth(int year, int month){
        log.debug("[start] WorkoutSessionService - listByMonth");
        CustomerUser customerUser = currentUserService.require();
        var result = jpaWorkoutPerformedRepository
                .findAllByMonth(customerUser.getId(), year, month).stream()
                .map(WorkoutPerformed::toGetResponse)
                .toList();
        log.debug("[finish] WorkoutSessionService - listByMonth");
        return result;
    }

    public ExerciseProgressResponse progress(UUID exerciseId){
        log.debug("[start] WorkoutSessionService - progress");
        Exercise exercise = exerciseRepository.findById(exerciseId);
        CustomerUser customerUser = currentUserService.require();

        Map<UUID, List<ExercisePerformedProgress>> bySession = new LinkedHashMap<>();
        performedRepository.listProgress(exerciseId, customerUser.getId())
                .forEach(row -> bySession
                        .computeIfAbsent(row.getWorkoutPerformedId(), key -> new ArrayList<>())
                        .add(row));

        List<ExerciseProgressPointResponse> points = bySession.values().stream()
                .map(this::toProgressPoint)
                .toList();

        log.debug("[finish] WorkoutSessionService - progress");
        return ExerciseProgressResponse.builder()
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .points(points)
                .build();
    }

    private ExerciseProgressPointResponse toProgressPoint(List<ExercisePerformedProgress> series){
        ExercisePerformedProgress first = series.get(0);

        int totalReps = series.stream()
                .map(ExercisePerformedProgress::getRepsPerformed)
                .filter(reps -> reps != null)
                .mapToInt(Short::intValue)
                .sum();

        float maxWeight = (float) series.stream()
                .map(ExercisePerformedProgress::getWeightPerformed)
                .filter(weight -> weight != null)
                .mapToDouble(Float::doubleValue)
                .max()
                .orElse(0d);

        List<ExercisePerformedGetResponse> mapped = series.stream()
                .map(row -> ExercisePerformedGetResponse.builder()
                        .exerciseId(row.getExerciseId())
                        .workoutPerformedId(row.getWorkoutPerformedId())
                        .serie(row.getSerie())
                        .repsGoal(row.getRepsGoal())
                        .repsPerformed(row.getRepsPerformed())
                        .weightGoal(row.getWeightGoal())
                        .weightPerformed(row.getWeightPerformed())
                        .build())
                .toList();

        return ExerciseProgressPointResponse.builder()
                .workoutPerformedId(first.getWorkoutPerformedId())
                .performedAt(first.getPerformedAt())
                .series(mapped)
                .totalReps(totalReps)
                .maxWeight(maxWeight)
                .build();
    }

    private WorkoutPerformed findOwnedSession(UUID performedId){
        WorkoutPerformed session = jpaWorkoutPerformedRepository.findById(performedId)
                .orElseThrow(WorkoutPerformedNotFoundException::new);
        CustomerUser customerUser = currentUserService.require();

        if (!session.getPerformedByUser().getId().equals(customerUser.getId())) {
            throw new ForbiddenException();
        }

        return session;
    }

    /** Goals are stored one entry per serie; a shorter array simply means no goal. */
    private <T> T goalAt(List<T> goals, Short serie){
        if (goals == null || serie == null || serie < 1 || serie > goals.size()) return null;
        return goals.get(serie - 1);
    }

    /** Weight columns are numeric(5,1), so values are stored at that exact scale. */
    private BigDecimal toDecimal(Float value){
        return value == null ? null : BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }
}

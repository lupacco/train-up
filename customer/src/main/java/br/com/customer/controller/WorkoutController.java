package br.com.customer.controller;

import br.com.customer.dto.request.CreateExerciseRequest;
import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.request.StartSessionRequest;
import br.com.customer.dto.response.ExerciseGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.dto.response.WorkoutPerformedGetResponse;
import br.com.customer.service.WorkoutService;
import br.com.customer.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workout")
@Slf4j
public class WorkoutController {

    private final WorkoutService workoutService;
    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutGetResponse> createWorkout(@RequestBody CreateWorkoutRequest createWorkoutRequest){
        log.debug("[start] WorkoutController - createWorkout");
        var response = workoutService.createWorkout(createWorkoutRequest);
        log.debug("[finish] WorkoutController - createWorkout");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{workoutId}/exercise")
    public ResponseEntity<List<ExerciseGetResponse>> createExercises(@PathVariable(name = "workoutId") UUID workoutId, @RequestBody List<CreateExerciseRequest> createExerciseRequest){
        log.debug("[start] WorkoutController - createExercise");
        var response = workoutService.createExercises(workoutId, createExerciseRequest);
        log.debug("[finish] WorkoutController - createExercise");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<WorkoutGetResponse>> listMyWorkouts(){
        log.debug("[start] WorkoutController - listMyWorkouts");
        var response = workoutService.listMyWorkouts();
        log.debug("[finish] WorkoutController - listMyWorkouts");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{workoutId}/exercises")
    public ResponseEntity<List<ExerciseGetResponse>> listAllWorkoutExercises(@PathVariable(name = "workoutId") UUID workoutId){
        log.debug("[start] WorkoutController - listAllWorkoutExercises");
        var response = workoutService.listAllWorkoutExercises(workoutId);
        log.debug("[finish] WorkoutController - listAllWorkoutExercises");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /** Starts a run of this workout, or resumes the one still open. */
    @PostMapping("/{workoutId}/performed")
    public ResponseEntity<WorkoutPerformedGetResponse> startSession(
            @PathVariable(name = "workoutId") UUID workoutId,
            @RequestBody(required = false) StartSessionRequest startSessionRequest){
        log.debug("[start] WorkoutController - startSession");
        LocalDate performedOn = startSessionRequest == null ? null : startSessionRequest.performedOn();
        var response = workoutSessionService.start(workoutId, performedOn);
        log.debug("[finish] WorkoutController - startSession");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workoutId}/performed")
    public ResponseEntity<List<WorkoutPerformedGetResponse>> history(@PathVariable(name = "workoutId") UUID workoutId){
        log.debug("[start] WorkoutController - history");
        var response = workoutSessionService.history(workoutId);
        log.debug("[finish] WorkoutController - history");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

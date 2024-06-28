package br.com.customer.controller;

import br.com.customer.dto.request.CreateExerciseRequest;
import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.ExerciseGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workout")
@Slf4j
public class WorkoutController {

    private final WorkoutService workoutService;

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

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WorkoutGetResponse>> listAllCustomerWorkouts(@PathVariable(name = "customerId") UUID customerId){
        log.debug("[start] CustomerUserController - listAllCustomerWorkouts");
        var response = workoutService.listAllCustomerWorkouts(customerId);
        log.debug("[finish] CustomerUserController - listAllCustomerWorkouts");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}

package br.com.customer.controller;

import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workout")
@Slf4j
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutGetResponse> createWorkout(@RequestBody CreateWorkoutRequest createWorkoutRequest){
        log.debug("[start] WorkoutController - createWorkout");
        var response = workoutService.create(createWorkoutRequest);
        log.debug("[finish] WorkoutController - createWorkout");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

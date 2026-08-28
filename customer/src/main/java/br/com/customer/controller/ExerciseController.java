package br.com.customer.controller;

import br.com.customer.dto.response.ExerciseProgressResponse;
import br.com.customer.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exercise")
@Slf4j
public class ExerciseController {

    private final WorkoutSessionService workoutSessionService;

    @GetMapping("/{exerciseId}/progress")
    public ResponseEntity<ExerciseProgressResponse> progress(@PathVariable(name = "exerciseId") UUID exerciseId){
        log.debug("[start] ExerciseController - progress");
        var response = workoutSessionService.progress(exerciseId);
        log.debug("[finish] ExerciseController - progress");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

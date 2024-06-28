package br.com.customer.service;

import br.com.customer.dto.request.CreateExerciseRequest;
import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.ExerciseGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.exception.WorkoutNotFoundException;
import br.com.customer.model.*;
import br.com.customer.repository.ExerciseRepository;
import br.com.customer.repository.WorkoutExerciseRepository;
import br.com.customer.repository.WorkoutRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final CustomerUserService customerUserService;

    @Transactional
    public WorkoutGetResponse createWorkout(CreateWorkoutRequest createWorkoutRequest){
        log.debug("[start] WorkoutService - createWorkout");
        CustomerUser customerUser = customerUserService.findById(createWorkoutRequest.userId());
        Set<CustomerUser> customerAttr = Collections.singleton(customerUser);
        Workout workout = Workout.builder()
                .name(createWorkoutRequest.name())
                .iconId(createWorkoutRequest.iconId())
                .createdByUser(customerUser)
                .assignedUsers(customerAttr)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
        Workout result = workoutRepository.save(workout);
        log.debug("[finish] WorkoutService - createWorkout");
        return result.toGetResponse();
    }

    @Transactional
    public List<ExerciseGetResponse> createExercises(UUID workoutId, List<CreateExerciseRequest> createExerciseRequest) {
        log.debug("[start] WorkoutService - createExercises");
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found!"));

        List<ExerciseGetResponse> response = createExerciseRequest.stream()
                .map(exerciseRequest -> {
                    Exercise exercise = exerciseRepository.save(Exercise.builder()
                            .name(exerciseRequest.name())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());

                    WorkoutExercise relation = workoutExerciseRepository.save(WorkoutExercise.builder()
                            .id(new WorkoutExerciseId(workout.getId(), exercise.getId()))
                            .series(exerciseRequest.series())
                            .repsGoals(exerciseRequest.repsGoals())
                            .weightGoals(exerciseRequest.weightGoals())
                            .build());

                    return ExerciseGetResponse.builder()
                            .name(exercise.getName())
                            .series(relation.getSeries())
                            .repsGoals(relation.getRepsGoals())
                            .weightGoals(relation.getWeightGoals())
                            .build();
                })
                .toList();
        log.debug("[finish] WorkoutService - createExercises");
        return response;
    }
}

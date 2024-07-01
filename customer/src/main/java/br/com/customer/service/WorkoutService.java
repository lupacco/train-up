package br.com.customer.service;

import br.com.customer.dto.request.CreateExerciseRequest;
import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.ExerciseGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.exception.WorkoutNotFoundException;
import br.com.customer.model.*;
import br.com.customer.repository.ExerciseRepository;
import br.com.customer.repository.jpa.JpaExerciseRepository;
import br.com.customer.repository.jpa.JpaWorkoutExerciseRepository;
import br.com.customer.repository.jpa.JpaWorkoutRepository;
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

    private final JpaWorkoutRepository jpaWorkoutRepository;
    private final JpaWorkoutExerciseRepository jpaWorkoutExerciseRepository;
    private final JpaExerciseRepository jpaExerciseRepository;
    private final CustomerUserService customerUserService;
    private final ExerciseRepository exerciseRepository;

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
        Workout result = jpaWorkoutRepository.save(workout);
        log.debug("[finish] WorkoutService - createWorkout");
        return result.toGetResponse();
    }

    public Workout findById(UUID workoutId){
        return jpaWorkoutRepository.findById(workoutId)
                .orElseThrow(WorkoutNotFoundException::new);
    }

    @Transactional
    public List<ExerciseGetResponse> createExercises(UUID workoutId, List<CreateExerciseRequest> createExerciseRequest) {
        log.debug("[start] WorkoutService - createExercises");
        Workout workout = findById(workoutId);
        List<ExerciseGetResponse> response = createExerciseRequest.stream()
                .map(exerciseRequest -> {
                    Exercise exercise = jpaExerciseRepository.save(Exercise.builder()
                            .name(exerciseRequest.name())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build());

                    WorkoutExercise relation = jpaWorkoutExerciseRepository.save(WorkoutExercise.builder()
                            .id(new WorkoutExerciseId(workout.getId(), exercise.getId()))
                            .series(exerciseRequest.series())
                            .repGoals(exerciseRequest.repsGoals())
                            .weightGoals(exerciseRequest.weightGoals())
                            .build());

                    return ExerciseGetResponse.builder()
                            .name(exercise.getName())
                            .series(relation.getSeries())
                            .repsGoals(relation.getRepGoals())
                            .weightGoals(relation.getWeightGoals())
                            .build();
                })
                .toList();
        log.debug("[finish] WorkoutService - createExercises");
        return response;
    }

    public List<WorkoutGetResponse> listAllCustomerWorkouts(UUID customerId) {
        log.debug("[start] WorkoutService - listAllCustomerWorkouts");
        customerUserService.findById(customerId);
        var result = jpaWorkoutRepository.findAllCustomerWorkouts(customerId).stream()
                .map(Workout::toGetResponse)
                .toList();
        log.debug("[finish] WorkoutService - listAllCustomerWorkouts");
        return result;
    }

    public List<ExerciseGetResponse> listAllWorkoutExercises(UUID workoutId) {
        log.debug("[start] WorkoutService - listAllWorkoutExercises");
        findById(workoutId);
//        using Jpa
//        List<Exercise> exercises = jpaExerciseRepository.findAllExercisesFromWorkout(workoutId);
//        List<ExerciseGetResponse> response = exercises.stream()
//                .map(exercise -> {
//                        WorkoutExercise relation = jpaWorkoutExerciseRepository.findWorkoutExerciseRelation(workoutId, exercise.getId());
//                        return ExerciseGetResponse.builder()
//                        .name(exercise.getName())
//                        .series(relation.getSeries())
//                        .repsGoals(relation.getRepGoals())
//                        .weightGoals(relation.getWeightGoals())
//                        .build();
//                })
//                .toList();


        List<ExerciseWorkoutGoals> exercises = exerciseRepository.listAllWorkoutExercisesWithGoals(workoutId);

        List<ExerciseGetResponse> response = exercises.stream()
            .map(exercise ->
                ExerciseGetResponse.builder()
                    .name(exercise.getName())
                    .series(exercise.getSeries())
                    .repsGoals(exercise.getRepGoals())
                    .weightGoals(exercise.getWeightGoals())
                    .build())
            .toList();

        log.debug("[finish] WorkoutService - listAllWorkoutExercises");
        return response;
    }
}

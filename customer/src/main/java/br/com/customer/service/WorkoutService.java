package br.com.customer.service;

import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.model.CustomerUser;
import br.com.customer.model.Workout;
import br.com.customer.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.geom.Area;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final CustomerService customerService;

    public WorkoutGetResponse create(CreateWorkoutRequest createWorkoutRequest){
        CustomerUser customerUser = customerService.findById(createWorkoutRequest.userId());
        log.debug("[start] WorkoutService - create");
        Workout workout = Workout.builder()
                .name(createWorkoutRequest.name())
                .iconId(createWorkoutRequest.iconId())
                .createdByUser(customerUser)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();

        Workout createdWorkout = workoutRepository.save(workout);
        log.debug("[finish] WorkoutService - create");
        return WorkoutGetResponse.builder()
                .workout(createdWorkout)
                .build();
    }
}

package br.com.customer.service;

import br.com.customer.dto.request.CreateWorkoutRequest;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.model.CustomerUser;
import br.com.customer.model.Workout;
import br.com.customer.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final CustomerUserService customerUserService;

    public WorkoutGetResponse create(CreateWorkoutRequest createWorkoutRequest){
        CustomerUser customerUser = customerUserService.findById(createWorkoutRequest.userId());
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

        Workout result = workoutRepository.save(workout);
        log.debug("[finish] WorkoutService - create");
        return WorkoutGetResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .iconId(result.getIconId())
                .createdByUser(result.getCreatedByUser().toGetResponse())
                .build();
    }
}

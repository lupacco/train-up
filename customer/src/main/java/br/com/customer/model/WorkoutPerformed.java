package br.com.customer.model;

import br.com.customer.dto.response.WorkoutPerformedGetResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_performed")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WorkoutPerformed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** Null while the session is still running. */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "performed_by_user", nullable = false)
    private CustomerUser performedByUser;

    public WorkoutPerformedGetResponse toGetResponse(){
        return WorkoutPerformedGetResponse.builder()
                .id(this.id)
                .workoutId(this.workout.getId())
                .workoutName(this.workout.getName())
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }
}

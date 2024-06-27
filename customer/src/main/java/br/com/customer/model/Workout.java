package br.com.customer.model;

import br.com.customer.dto.response.WorkoutGetResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "workout")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column(name = "icon_id")
    private UUID iconId;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "created_by_user", nullable = false)
    private CustomerUser createdByUser;

    @ManyToMany(mappedBy = "assignedWorkouts")
    private Set<CustomerUser> customerUserSet;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(
            name = "workout_exercise",
            joinColumns = @JoinColumn(name = "workout_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private Set<Exercise> assignedExercises;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public WorkoutGetResponse toGetResponse(){
        return WorkoutGetResponse.builder()
                .id(this.id)
                .name(this.name)
                .iconId(this.iconId)
                .createdByUser(this.createdByUser.toGetResponse())
                .build();
    }
}

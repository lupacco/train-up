package br.com.customer.model;

import br.com.customer.dto.response.CustomerUserGetResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "customer_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthdate;

    @OneToMany(mappedBy = "createdByUser")
    private Set<Workout> createdWorkouts;

    @ManyToMany
    @JoinTable(
            name = "user_workout",
            joinColumns = @JoinColumn(name = "customer_user_id")
            ,inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private Set<Workout> assignedWorkouts;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public CustomerUserGetResponse toGetResponse(){
        return CustomerUserGetResponse.builder()
                .id(this.id)
                .name(this.name)
                .username(this.username)
                .email(this.email)
                .birthdate(this.birthdate)
                .build();
    }
}

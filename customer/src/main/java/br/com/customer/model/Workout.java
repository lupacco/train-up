package br.com.customer.model;

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;
}

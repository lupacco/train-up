package br.com.customer.repository;

import br.com.customer.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

    @Query(value = """
            SELECT w.* FROM workout w
            JOIN user_workout uw ON uw.workout_id = w.id
            JOIN customer_user cu ON cu.id = uw.customer_user_id
            WHERE cu.id = :customerId
            """, nativeQuery = true)
    List<Workout> findAllCustomerWorkouts(@Param("customerId") UUID customerId);
}

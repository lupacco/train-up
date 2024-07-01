package br.com.customer.repository.jpa;

import br.com.customer.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaWorkoutRepository extends JpaRepository<Workout, UUID> {

    @Query(value = """
            select w.* from workout w
            join user_workout uw on uw.workout_id = w.id
            join customer_user cu on cu.id = uw.customer_user_id
            where cu.id = :customerId
            """, nativeQuery = true)
    List<Workout> findAllCustomerWorkouts(@Param("customerId") UUID customerId);
}

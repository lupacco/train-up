package br.com.customer.repository.jpa;

import br.com.customer.model.WorkoutPerformed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaWorkoutPerformedRepository extends JpaRepository<WorkoutPerformed, UUID> {

    @Query(value = """
            select wp.* from workout_performed wp
            where wp.workout_id = :workoutId and wp.performed_by_user = :customerId
            order by wp.start_time desc
            """, nativeQuery = true)
    List<WorkoutPerformed> findAllByWorkoutAndUser(@Param("workoutId") UUID workoutId,
                                                   @Param("customerId") UUID customerId);

    /** A session left open, so remounting the logging screen resumes instead of duplicating. */
    @Query(value = """
            select wp.* from workout_performed wp
            where wp.workout_id = :workoutId
              and wp.performed_by_user = :customerId
              and wp.end_time is null
            order by wp.start_time desc
            limit 1
            """, nativeQuery = true)
    Optional<WorkoutPerformed> findOpenSession(@Param("workoutId") UUID workoutId,
                                               @Param("customerId") UUID customerId);

    @Query(value = """
            select wp.* from workout_performed wp
            where wp.performed_by_user = :customerId
              and cast(wp.start_time as date) = :day
            order by wp.start_time desc
            """, nativeQuery = true)
    List<WorkoutPerformed> findAllByDay(@Param("customerId") UUID customerId,
                                        @Param("day") LocalDate day);

    @Query(value = """
            select wp.* from workout_performed wp
            where wp.performed_by_user = :customerId
              and extract(year from wp.start_time) = :year
              and extract(month from wp.start_time) = :month
            order by wp.start_time desc
            """, nativeQuery = true)
    List<WorkoutPerformed> findAllByMonth(@Param("customerId") UUID customerId,
                                          @Param("year") int year,
                                          @Param("month") int month);
}

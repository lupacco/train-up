package br.com.customer.repository;

import br.com.customer.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

}

package br.com.customer.repository.jpa;

import br.com.customer.model.Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaIconRepository extends JpaRepository<Icon, UUID> {
}

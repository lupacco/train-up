package br.com.customer.repository;

import br.com.customer.model.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerUserRepository extends JpaRepository<CustomerUser, UUID> {
    Optional<CustomerUser> findByUsername(String username);

    Optional<CustomerUser> findByEmail(String email);
}

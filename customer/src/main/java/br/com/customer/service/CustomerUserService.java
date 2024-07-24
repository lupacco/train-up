package br.com.customer.service;

import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.exception.UserEmailNotFoundException;
import br.com.customer.exception.UserNotFoundException;
import br.com.customer.model.CustomerUser;
import br.com.customer.repository.jpa.JpaCustomerUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomerUserService {

    private final JpaCustomerUserRepository jpaCustomerUserRepository;
    private final JwtService jwtService;

    public CustomerUserGetResponse findByUsername(String username){
        log.debug("[start] CustomerService - findByUsername");
        var result = jpaCustomerUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.debug("[finish] CustomerService - findByUsername");
        return result.toGetResponse();
    }

    public CustomerUser findByEmail(String email){
        log.debug("[start] CustomerService - findByEmail");
        var result = jpaCustomerUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException("User not found"));
        log.debug("[finish] CustomerService - findByEmail");
        return result;
    }

    public CustomerUser findById(UUID customerId) {
        log.debug("[start] CustomerService - findById");
        var result = jpaCustomerUserRepository.findById(customerId)
                .orElseThrow(UserNotFoundException::new);
        log.debug("[finish] CustomerService - findById");
        return result;
    }

    public CustomerUserGetResponse findByToken(String authorization) {
        log.debug("[start] CustomerService - findByToken");
        String token = authorization.split(" ")[1];
        String username = jwtService.extractUsername(token);

        var result = jpaCustomerUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.debug("[finish] CustomerService - findByToken");
        return result.toGetResponse();
    }
}

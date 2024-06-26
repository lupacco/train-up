package br.com.customer.service;

import br.com.customer.dto.request.RegisterRequest;
import br.com.customer.exception.UserEmailNotFoundException;
import br.com.customer.model.CustomerUser;
import br.com.customer.repository.CustomerUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomerService {

    private final CustomerUserRepository customerUserRepository;

    public CustomerUser findByUsername(String username){
        log.debug("[start] CustomerService - findByUsername");
        var result = customerUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.debug("[finish] CustomerService - findByUsername");
        return result;
    }

    public CustomerUser findByEmail(String email){
        log.debug("[start] CustomerService - findByEmail");
        var result = customerUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException("User not found"));
        log.debug("[finish] CustomerService - findByEmail");
        return result;
    }

}

package br.com.service;

import br.com.core.request.RegisterPostRequest;
import br.com.model.Customer;
import br.com.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer save(RegisterPostRequest request) {
        log.debug("[start] CustomerService - save");
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .birthdate(request.birthdate())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var response = customerRepository.save(customer);
        log.debug("[finish] CustomerService - save");
        return response;
    }

    public Customer findByUsername(String username){
        log.debug("[start] CustomerService - findByUsername");
        var result = customerRepository.findByUsername(username);
        log.debug("[finish] CustomerService - findByUsername");
        return result;
    }

    public Customer findByEmail(String email){
        log.debug("[start] CustomerService - findByEmail");
        var result = customerRepository.findByEmail(email);
        log.debug("[finish] CustomerService - findByEmail");
        return result;
    }

//    public Customer findByEmailAndPassword(AuthPostRequest request){
//        log.debug("[start] CustomerService - findByUsernameAndPassword");
//        Customer customer = customerRepository.findByUsernameAndPassword(request.username(), request.password());
//
//        var response = customerRepository.save(customer);
//        log.debug("[finish] CustomerService - findByUsernameAndPassword");
//        return response;
//    }
}

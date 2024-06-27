package br.com.customer.service;

import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.exception.UserEmailNotFoundException;
import br.com.customer.model.CustomerUser;
import br.com.customer.model.Workout;
import br.com.customer.repository.CustomerUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomerUserService {

    private final CustomerUserRepository customerUserRepository;

    public CustomerUserGetResponse findByUsername(String username){
        log.debug("[start] CustomerService - findByUsername");
        var result = customerUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.debug("[finish] CustomerService - findByUsername");
        return result.toGetResponse();
    }

    public CustomerUser findByEmail(String email){
        log.debug("[start] CustomerService - findByEmail");
        var result = customerUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException("User not found"));
        log.debug("[finish] CustomerService - findByEmail");
        return result;
    }

    public CustomerUser findById(UUID customerId) {
        log.debug("[start] CustomerService - findById");
        var result = customerUserRepository.findById(customerId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        log.debug("[finish] CustomerService - findById");
        return result;
    }

    public List<WorkoutGetResponse> listAllCustomerWorkouts(UUID customerId) {
        log.debug("[start] CustomerService - listAllCustomerWorkouts");
        CustomerUser customerUser = findById(customerId);
        var result = customerUser.getAssignedWorkouts().stream()
                .map(Workout::toGetResponse)
                .toList();
        log.debug("[finish] CustomerService - listAllCustomerWorkouts");
        return result;
    }
}

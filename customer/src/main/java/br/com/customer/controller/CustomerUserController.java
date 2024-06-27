package br.com.customer.controller;

import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.dto.response.WorkoutGetResponse;
import br.com.customer.service.CustomerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
@Slf4j
public class CustomerUserController {

    private final CustomerUserService customerUserService;

    @GetMapping("/{customerUsername}")
    public ResponseEntity<CustomerUserGetResponse> findById(@PathVariable(name = "customerUsername") String customerUsername){
        log.debug("[start] CustomerUserController - findById");
        var response = customerUserService.findByUsername(customerUsername);
        log.debug("[finish] CustomerUserController - findById");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{customerId}/workout")
    public ResponseEntity<List<WorkoutGetResponse>> listAllCustomerWorkouts(@PathVariable(name = "customerId") UUID customerId){
        log.debug("[start] CustomerUserController - listAllCustomerWorkouts");
        var response = customerUserService.listAllCustomerWorkouts(customerId);
        log.debug("[finish] CustomerUserController - listAllCustomerWorkouts");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

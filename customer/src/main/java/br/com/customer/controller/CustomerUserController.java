package br.com.customer.controller;

import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.service.CustomerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping
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

}

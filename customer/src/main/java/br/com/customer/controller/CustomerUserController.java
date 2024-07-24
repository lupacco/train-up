package br.com.customer.controller;

import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.service.CustomerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/token")
    public ResponseEntity<CustomerUserGetResponse> findByToken(@RequestHeader(name = "Authorization") String authorization){
        log.debug("[start] CustomerUserController - findByToken");
        var response = customerUserService.findByToken(authorization);
        log.debug("[finish] CustomerUserController - findByToken");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

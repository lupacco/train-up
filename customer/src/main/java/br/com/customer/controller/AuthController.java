package br.com.customer.controller;

import br.com.customer.dto.request.AuthenticationRequest;
import br.com.customer.dto.request.RegisterRequest;
import br.com.customer.dto.response.AuthenticationResponse;
import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.model.CustomerUser;
import br.com.customer.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<CustomerUserGetResponse> register(@RequestBody RegisterRequest registerRequest){
        log.debug("[start] AuthController - register");
        var response = authService.register(registerRequest);
        log.debug("[finish] AuthController - register");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        log.debug("[start] AuthController - authenticate");
        var response = authService.authenticate(authenticationRequest);
        log.debug("[finish] AuthController - authenticate");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
//    @PostMapping("/authenticate")
//    public ResponseEntity<AuthenticationResponse> authenticate(Authentication authenticationRequest){
//        log.debug("[start] AuthController - authenticate");
//        var response = authService.authenticate(authenticationRequest);
//        log.debug("[finish] AuthController - authenticate");
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.OK).body("Yoyoyo");
    }

}

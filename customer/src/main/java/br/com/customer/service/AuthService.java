package br.com.customer.service;

import br.com.customer.dto.request.AuthenticationRequest;
import br.com.customer.dto.request.RegisterRequest;
import br.com.customer.dto.response.AuthenticationResponse;
import br.com.customer.dto.response.CustomerUserGetResponse;
import br.com.customer.exception.ConflictException;
import br.com.customer.model.CustomerUser;
import br.com.customer.repository.jpa.JpaCustomerUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JpaCustomerUserRepository jpaCustomerUserRepository;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;


    public CustomerUserGetResponse register(RegisterRequest registerRequest){
        log.debug("[start] AuthService - register");
        if(jpaCustomerUserRepository.findByUsername(registerRequest.username()).isPresent()) throw new ConflictException("Username already in use.");
        if(jpaCustomerUserRepository.findByEmail(registerRequest.email()).isPresent()) throw new ConflictException("Email already in use.");
        CustomerUser customer = CustomerUser.builder()
                .id(UUID.randomUUID())
                .name(registerRequest.name())
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .birthdate(registerRequest.birthdate())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CustomerUser result = jpaCustomerUserRepository.save(customer);
        log.debug("[finish] AuthService - register");

        return CustomerUserGetResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .username(result.getUsername())
                .email(result.getEmail())
                .birthdate(result.getBirthdate())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(),
                authenticationRequest.password()
        );

        authenticationProvider.authenticate(authentication);

        String token = jwtService.generateToken(authentication);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}

package br.com.service;

import br.com.core.request.AuthPostRequest;
import br.com.core.request.RegisterPostRequest;
import br.com.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerService customerService;

    public Customer register(RegisterPostRequest request){
//        if(customerService.findByUsername(request.username()) != null) throw new ConflictException("Username already in use.");
//        if(customerService.findByEmail(request.email()) != null) throw new ConflictException("Email already in use.");

        return customerService.save(request);
    }

    public Customer authenticate(AuthPostRequest request) {
        return null;
    }
}

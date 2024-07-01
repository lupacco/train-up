package br.com.customer.model;

import br.com.customer.repository.jpa.JpaCustomerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaCustomerUserRepository jpaCustomerUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return jpaCustomerUserRepository.findByUsername(username)
        .map(UserAuthenticated::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}

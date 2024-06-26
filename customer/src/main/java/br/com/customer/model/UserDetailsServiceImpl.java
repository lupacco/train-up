package br.com.customer.model;

import br.com.customer.repository.CustomerUserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerUserRepository customerUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return customerUserRepository.findByUsername(username)
        .map(UserAuthenticated::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}

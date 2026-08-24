package br.com.customer.service;

import br.com.customer.exception.UnauthorizedException;
import br.com.customer.exception.UserNotFoundException;
import br.com.customer.model.CustomerUser;
import br.com.customer.repository.jpa.JpaCustomerUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {

    private final JpaCustomerUserRepository jpaCustomerUserRepository;

    /**
     * Resolves the caller from the security context. The JWT subject carries the
     * username (see JwtService#generateToken), so no id is ever trusted from the
     * request body or path.
     */
    public CustomerUser require(){
        log.debug("[start] CurrentUserService - require");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException();
        }

        CustomerUser result = jpaCustomerUserRepository.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);
        log.debug("[finish] CurrentUserService - require");
        return result;
    }
}

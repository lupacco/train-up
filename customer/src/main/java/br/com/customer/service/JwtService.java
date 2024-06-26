package br.com.customer.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication){
        Instant now = Instant.now();
        long expires = 3500L;
        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        var claims = JwtClaimsSet.builder()
                .issuer("train-up-jwt")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expires))
                .subject(authentication.getName())
                .claim("scope", scopes)
                .build();


        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

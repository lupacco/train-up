package br.com.customer.dto.response;

import br.com.customer.model.CustomerUser;
import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}

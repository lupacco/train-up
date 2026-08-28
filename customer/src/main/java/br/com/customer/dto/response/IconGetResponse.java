package br.com.customer.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record IconGetResponse(
        UUID id,
        String name,
        String url
) {
}

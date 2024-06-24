package br.com.core.request;

import lombok.Getter;

import java.util.Date;

public record AuthPostRequest(
        String username,
        String password
) {
}

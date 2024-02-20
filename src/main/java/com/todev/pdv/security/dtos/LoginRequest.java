package com.todev.pdv.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O login não pode ser nulo!")
        String login,
        @NotBlank(message = "A senha não pode ser nula!")
        String password) {
}

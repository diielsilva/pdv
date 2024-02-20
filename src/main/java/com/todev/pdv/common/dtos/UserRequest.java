package com.todev.pdv.common.dtos;

import com.todev.pdv.common.constraints.contracts.Role;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "O nome não pode ser nulo!")
        String name,
        @NotBlank(message = "O login não pode ser nulo!")
        String login,
        @NotBlank(message = "A senha não pode ser nula!")
        String password,
        @NotBlank(message = "O papel do usuário não pode ser nulo!")
        @Role
        String role) {
}

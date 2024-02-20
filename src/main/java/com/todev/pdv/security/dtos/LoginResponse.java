package com.todev.pdv.security.dtos;

import com.todev.pdv.core.enums.Role;

public record LoginResponse(String token, Role role) {
}

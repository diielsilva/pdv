package com.todev.pdv.common.dtos;

import com.todev.pdv.core.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(Integer id,
                           String name,
                           Role role,
                           LocalDateTime createdAt,
                           LocalDateTime deletedAt) {
}

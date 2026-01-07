package com.todev.pdv.common.dtos;

import java.time.LocalDateTime;

public record ProductResponseBody(
        Integer id,
        String description,
        Integer amount,
        Double price,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}

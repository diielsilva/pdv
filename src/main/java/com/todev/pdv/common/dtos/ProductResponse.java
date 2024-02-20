package com.todev.pdv.common.dtos;

import java.time.LocalDateTime;

public record ProductResponse(Integer id,
                              String description,
                              Integer amount,
                              Double price,
                              LocalDateTime createdAt,
                              LocalDateTime deletedAt) {
}

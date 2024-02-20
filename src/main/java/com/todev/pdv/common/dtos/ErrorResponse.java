package com.todev.pdv.common.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public record ErrorResponse(LocalDateTime timestamps,
                            Integer status,
                            String message,
                            String path,
                            Set<String> details) {
}

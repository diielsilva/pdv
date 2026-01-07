package com.todev.pdv.common.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public record ErrorResponseBody(
        LocalDateTime timestamps,
        int status,
        String message,
        String path,
        Set<String> details
) {
}

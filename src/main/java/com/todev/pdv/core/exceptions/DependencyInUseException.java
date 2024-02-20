package com.todev.pdv.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DependencyInUseException extends RuntimeException {
    private final String message;
}

package com.todev.pdv.core.exceptions;

public class PermissionDeniedException extends RuntimeException {
    private final String message;

    public PermissionDeniedException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

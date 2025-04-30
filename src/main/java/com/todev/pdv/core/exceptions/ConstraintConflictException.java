package com.todev.pdv.core.exceptions;

public class ConstraintConflictException extends RuntimeException {
    private final String message;

    public ConstraintConflictException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

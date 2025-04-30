package com.todev.pdv.core.exceptions;

public class DependencyInUseException extends RuntimeException {
    private final String message;

    public DependencyInUseException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

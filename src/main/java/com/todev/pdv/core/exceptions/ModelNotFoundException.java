package com.todev.pdv.core.exceptions;

public class ModelNotFoundException extends RuntimeException {
    private final String message;

    public ModelNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

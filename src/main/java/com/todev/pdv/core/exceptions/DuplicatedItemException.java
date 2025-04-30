package com.todev.pdv.core.exceptions;

public class DuplicatedItemException extends RuntimeException {
    private final String message;

    public DuplicatedItemException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

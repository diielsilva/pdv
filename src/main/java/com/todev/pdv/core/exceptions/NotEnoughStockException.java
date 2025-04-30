package com.todev.pdv.core.exceptions;

public class NotEnoughStockException extends RuntimeException {
    private final String message;

    public NotEnoughStockException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

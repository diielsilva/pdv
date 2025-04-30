package com.todev.pdv.core.exceptions;

public class FileExportException extends RuntimeException {
    private final String message;

    public FileExportException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

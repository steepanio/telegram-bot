package com.stepanio.java.training.telegram.app.exceptions;

public class FileNotFoundInStorageException extends RuntimeException {
    public FileNotFoundInStorageException(String message) {
        super(message);
    }
}

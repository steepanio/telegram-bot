package com.stepanio.java.training.telegram.app.exception;

public class VacanciesNotFoundException extends RuntimeException {
    public VacanciesNotFoundException(String message) {
        super(message);
    }
}

package com.banquito.core.shared.exception;

public class NotFoundException extends ResourceNotFoundException {

    public NotFoundException(String message) {
        super(message);
    }
}
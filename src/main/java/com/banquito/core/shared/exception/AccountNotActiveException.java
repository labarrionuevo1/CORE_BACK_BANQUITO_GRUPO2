package com.banquito.core.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotActiveException extends RuntimeException {
    
    public AccountNotActiveException(String message) {
        super(message);
    }
    
    public AccountNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.banquito.core.shared.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(false, code, message, path, LocalDateTime.now());
    }
}
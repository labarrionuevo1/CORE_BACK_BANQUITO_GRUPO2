package com.banquito.core.shared.exception;

import com.banquito.core.shared.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        log.warn("Recurso duplicado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientFunds(InsufficientFundsException ex) {
        log.warn("Fondos insuficientes: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotActive(AccountNotActiveException ex) {
        log.warn("Cuenta no activa: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ApiResponse<Void>> handleIdempotency(IdempotencyException ex) {
        log.warn("Error de idempotencia: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        log.warn("Error de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Error de validación de campos: {}", msg);
        return ResponseEntity.badRequest().body(ApiResponse.fail(msg, null));
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        log.warn("Violación de constraint: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), null));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Violación de integridad de datos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail("Violación de integridad de datos", null));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Error no controlado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail("Error interno del servidor", null));
    }
}

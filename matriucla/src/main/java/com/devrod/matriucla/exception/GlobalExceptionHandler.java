package com.devrod.matriucla.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta los errores de validación (@Valid, @NotNull, @NotBlank)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Extraemos solo el primer error para devolver un JSON limpio y directo
        FieldError firstError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        
        if (firstError != null) {
            errors.put("error", firstError.getField() + ": " + firstError.getDefaultMessage());
        } else {
            errors.put("error", "Error de validación en la solicitud");
        }
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Intercepta nuestros errores manuales de negocio (ej: throw new RuntimeException("Matrícula ya cancelada"))
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeExceptions(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Intercepta cualquier otro error no controlado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno del servidor: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

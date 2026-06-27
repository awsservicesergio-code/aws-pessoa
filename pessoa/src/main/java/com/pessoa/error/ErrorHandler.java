package com.pessoa.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.error.exceptions.SqsErrorException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    /**
     * Método responsável por tratar exception MethodArgumentNotValidException.
     * @param ex
     * @return ResponseEntity<Map<String, String>>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((FieldError error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Método responsável por tratar exception ConstraintViolationException.
     * @param ex
     * @return ResponseEntity<Map<String, String>>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.put(v.getPropertyPath().toString(), v.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Método responsável por tratar exception HandlerMethodValidationException.
     * @param ex
     * @return ResponseEntity<Map<String, String>>
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getAllValidationResults().forEach(result -> {
            result.getResolvableErrors().forEach(err -> {
                errors.put(result.getMethodParameter().getParameterName(), err.getDefaultMessage());
            });
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Método responsável por tratar exception SqsErrorException.
     * @param ex
     * @return ResponseEntity<String>
     */
    @ExceptionHandler(SqsErrorException.class)
    public ResponseEntity<String> handleSqsErrorException(SqsErrorException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Método responsável por tratar exception JsonProcessingException.
     * @param ex
     * @return ResponseEntity<String>
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Método responsável por tratar exception Exception.
     * @param ex
     * @return ResponseEntity<String>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}

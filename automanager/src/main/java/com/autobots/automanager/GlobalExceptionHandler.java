package com.autobots.automanager;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String msg = "Violação de integridade de dados. Verifique se não está cadastrando e-mail, documento ou outro campo único já existente.";
        Throwable cause = ex.getRootCause();
        if (cause instanceof SQLException && cause.getMessage() != null) {
            String lower = cause.getMessage().toLowerCase();
            if (lower.contains("documento") && lower.contains("unique")) {
                msg = "Já existe um documento com este número cadastrado.";
            } else if (lower.contains("email") && lower.contains("unique")) {
                msg = "Já existe um e-mail cadastrado com este endereço.";
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
    }
} 
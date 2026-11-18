package com.sonidovivo.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> error(HttpStatus estado, String mensaje, List<String> detalles) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", mensaje, "detalles", detalles));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    // Un id que no es número (por ejemplo /productos/abc) tampoco corresponde a un producto.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> idInvalido(MethodArgumentTypeMismatchException e) {
        return error(HttpStatus.NOT_FOUND, "Producto no encontrado.", List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> rutaInexistente(NoResourceFoundException e) {
        return error(HttpStatus.NOT_FOUND, "Ruta no encontrada.", List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> entradaInvalida(MethodArgumentNotValidException e) {
        List<String> detalles = e.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getDefaultMessage())
                .distinct()
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Producto inválido.", detalles);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "JSON mal formado o con tipos incorrectos.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> interno(Exception e) {
        log.error("Error interno al procesar la solicitud", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible procesar la solicitud.", List.of());
    }
}
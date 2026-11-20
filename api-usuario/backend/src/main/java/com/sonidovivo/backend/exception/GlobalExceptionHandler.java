package com.sonidovivo.backend.exception;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Map<String, Object>> error(HttpStatusCode estado,
                                                      String mensaje, List<String> detalles) {
        return ResponseEntity.status(estado)
                .body(Map.of("mensaje", mensaje, "detalles", detalles));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> ausente(RecursoNoEncontradoException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage(), List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> ruta(NoResourceFoundException e) {
        return error(HttpStatus.NOT_FOUND, "Ruta no encontrada.", List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estado(ResponseStatusException e) {
        String motivo = e.getReason() == null ? "Solicitud rechazada." : e.getReason();
        return error(e.getStatusCode(), motivo, List.of());
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<Map<String, Object>> framework(ErrorResponseException e) {
        return error(e.getStatusCode(), "Solicitud rechazada.", List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        var detalles = e.getBindingResult().getFieldErrors().stream()
                .map(c -> c.getField() + " " + c.getDefaultMessage()).distinct().toList();
        return error(HttpStatus.BAD_REQUEST, "Revisa los datos ingresados.", detalles);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
                       HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> formato(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Formato de datos incorrecto.", List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException e) {
        return error(HttpStatus.CONFLICT, "Los datos chocan con un registro existente.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> interno(Exception e) {
        LOG.error("Error interno al procesar la solicitud", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible procesar la solicitud.", List.of());
    }
}

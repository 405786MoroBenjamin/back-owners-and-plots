package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.ErrorApi;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

/**
 * Clase de configuración de ControllerException.
 */
@ControllerAdvice
public class ControllerException {

    /**
     * Metodo para manejo de excepción de clase INTERNAL_SERVER_ERROR.
     *
     * @return una respuesta de INTERNAL_SERVER_ERROR.
     * @param e excepción.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApi> handleError(Exception e) {
        ErrorApi error = buildError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Metodo para manejo de excepción de clase BAD_REQUEST.
     *
     * @return una respuesta de BAD_REQUEST.
     * @param e excepción.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleError(MethodArgumentNotValidException e) {
        ErrorApi error = buildError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Metodo para manejo de excepción de clase NOT_FOUND.
     *
     * @return una respuesta de NOT_FOUND.
     * @param e excepción.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorApi> handleError(EntityNotFoundException e) {
        ErrorApi error = buildError(e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Metodo para arrojar errores de tipo HTTP.
     *
     * @return una respuesta de error tipo HTTP.
     * @param e excepción.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorApi> handleError(ResponseStatusException e) {
        ErrorApi error = buildError(e.getReason(), HttpStatus.valueOf(e.getStatusCode().value()));
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    /**
     * Metodo para construir un objeto error API.
     *
     * @param message mensaje de error a arrojar.
     * @param status código de HTTP.
     * @return un ErrorApi.
     */
    private ErrorApi buildError(String message, HttpStatus status) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(message)
                .build();
    }
}

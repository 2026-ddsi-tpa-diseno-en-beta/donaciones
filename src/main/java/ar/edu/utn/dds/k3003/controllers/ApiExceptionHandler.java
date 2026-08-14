package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.exceptions.DonacionRechazadaException;
import ar.edu.utn.dds.k3003.exceptions.IntegracionException;
import ar.edu.utn.dds.k3003.exceptions.RecursoEnUsoException;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  /** No existe el recurso pedido. */
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("NOT_FOUND", exception.getMessage()));
  }

  /** El pedido esta mal formado o viola una precondicion de los datos. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("BAD_REQUEST", exception.getMessage()));
  }

  /** La donacion se rechaza por regla de negocio, no por un error del cliente. */
  @ExceptionHandler(DonacionRechazadaException.class)
  public ResponseEntity<ErrorResponse> handleRechazo(DonacionRechazadaException exception) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(new ErrorResponse("DONACION_RECHAZADA", exception.getMessage()));
  }

  /** Se intento borrar algo que esta en uso. */
  @ExceptionHandler(RecursoEnUsoException.class)
  public ResponseEntity<ErrorResponse> handleEnUso(RecursoEnUsoException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("RECURSO_EN_USO", exception.getMessage()));
  }

  /**
   * Fallo un componente del que dependemos. Se responde 502 y no 400: el problema no esta en el
   * pedido que nos hicieron.
   */
  @ExceptionHandler(IntegracionException.class)
  public ResponseEntity<ErrorResponse> handleIntegracion(IntegracionException exception) {
    log.warn("Fallo la integracion con {}: {}", exception.getComponente(), exception.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new ErrorResponse("ERROR_INTEGRACION", exception.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("BAD_REQUEST", exception.getMessage()));
  }

  public record ErrorResponse(String code, String message) {}
}

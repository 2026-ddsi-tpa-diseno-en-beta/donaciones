package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.Getter;

/** Un punto del historial de estados de una donacion. Inmutable: es un registro de auditoria. */
@Embeddable
@Getter
public class CambioEstadoDonacion {
  @Enumerated(EnumType.STRING)
  private EstadoDonacion estado;

  private LocalDateTime fechaCambio;
  private String detalle;

  protected CambioEstadoDonacion() {}

  public CambioEstadoDonacion(EstadoDonacion estado, String detalle) {
    this.estado = estado;
    this.fechaCambio = LocalDateTime.now();
    this.detalle = detalle;
  }
}

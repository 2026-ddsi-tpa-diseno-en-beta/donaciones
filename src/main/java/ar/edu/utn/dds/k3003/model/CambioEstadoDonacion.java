package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CambioEstadoDonacion {
  @Enumerated(EnumType.STRING)
  private EstadoDonacionEnum estado;

  private LocalDateTime fechaCambio;
  private String detalle;

  protected CambioEstadoDonacion() {}

  public CambioEstadoDonacion(EstadoDonacionEnum estado, String detalle) {
    this.estado = estado;
    this.fechaCambio = LocalDateTime.now();
    this.detalle = detalle;
  }
}

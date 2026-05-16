package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioEstadoDonacion {
  private EstadoDonacionEnum estado;
  private LocalDateTime fechaCambio;
  private String detalle;

  public CambioEstadoDonacion(EstadoDonacionEnum estado, String detalle) {
    this.estado = estado;
    this.fechaCambio = LocalDateTime.now();
    this.detalle = detalle;
  }
}

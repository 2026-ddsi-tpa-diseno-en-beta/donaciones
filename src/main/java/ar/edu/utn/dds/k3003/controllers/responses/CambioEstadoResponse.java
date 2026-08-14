package ar.edu.utn.dds.k3003.controllers.responses;

import ar.edu.utn.dds.k3003.model.CambioEstadoDonacion;
import java.time.LocalDateTime;

/** Un punto del historial de estados, para poder auditar sin mirar la base de datos. */
public record CambioEstadoResponse(String estado, LocalDateTime fechaCambio, String detalle) {

  public static CambioEstadoResponse desde(CambioEstadoDonacion cambio) {
    return new CambioEstadoResponse(
        cambio.getEstado().name(), cambio.getFechaCambio(), cambio.getDetalle());
  }
}

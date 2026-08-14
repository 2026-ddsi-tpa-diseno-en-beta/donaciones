package ar.edu.utn.dds.k3003.model;

/**
 * Estado de una donacion dentro del dominio.
 *
 * <p>Es un enum propio y no el {@code EstadoDonacionEnum} de la catedra: el modelo de dominio no
 * debe depender de los DTOs. La traduccion entre ambos se hace en {@code DonacionesMapper}, que es
 * la frontera del modulo.
 */
public enum EstadoDonacion {
  INGRESADA,
  ACEPTADA,
  CONQUEJA;

  /** Indica si desde este estado se puede pasar al estado indicado. */
  public boolean permiteTransicionA(EstadoDonacion nuevoEstado) {
    if (ACEPTADA.equals(nuevoEstado)) {
      return INGRESADA.equals(this);
    }
    if (CONQUEJA.equals(nuevoEstado)) {
      return ACEPTADA.equals(this);
    }
    return false;
  }
}

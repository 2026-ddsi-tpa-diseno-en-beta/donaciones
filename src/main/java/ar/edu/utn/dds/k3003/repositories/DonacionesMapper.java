package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.EstadoDonacion;

/** Frontera entre el dominio y los DTOs de la catedra: aca se traduce el enum de estado. */
public class DonacionesMapper {

  public DonacionDTO toDTO(Donacion donacion) {
    return new DonacionDTO(
        donacion.getId(),
        donacion.getDonadorId(),
        donacion.getDepositoId(),
        donacion.getDescripcion(),
        donacion.getProductoId(),
        donacion.getCantidad(),
        toDTO(donacion.getEstadoActual()));
  }

  public EstadoDonacionEnum toDTO(EstadoDonacion estado) {
    if (estado == null) {
      return null;
    }
    return switch (estado) {
      case INGRESADA -> EstadoDonacionEnum.INGRESADA;
      case ACEPTADA -> EstadoDonacionEnum.ACEPTADA;
      case CONQUEJA -> EstadoDonacionEnum.CONQUEJA;
    };
  }

  public EstadoDonacion toModel(EstadoDonacionEnum estado) {
    if (estado == null) {
      return null;
    }
    return switch (estado) {
      case INGRESADA -> EstadoDonacion.INGRESADA;
      case ACEPTADA -> EstadoDonacion.ACEPTADA;
      case CONQUEJA -> EstadoDonacion.CONQUEJA;
    };
  }
}

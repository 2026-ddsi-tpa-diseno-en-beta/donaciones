package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.Donacion;

public class DonacionesMapper {

  public DonacionDTO toDTO(Donacion donacion) {
    return new DonacionDTO(
        donacion.getId(),
        donacion.getDonadorId(),
        donacion.getDepositoId(),
        donacion.getDescripcion(),
        donacion.getProductoId(),
        donacion.getCantidad(),
        donacion.getEstadoActual());
  }
}

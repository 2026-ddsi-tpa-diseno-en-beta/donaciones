package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.model.Identificador;

public class IdentificadorMapper {

  public Identificador toModel(IdentificadorDTO identificadorDTO) {
    return new Identificador(identificadorDTO.tipo(), identificadorDTO.descripcion());
  }

  public IdentificadorDTO toDTO(Identificador identificador) {
    return new IdentificadorDTO(
        identificador.getId(), identificador.getTipo(), identificador.getDescripcion());
  }
}

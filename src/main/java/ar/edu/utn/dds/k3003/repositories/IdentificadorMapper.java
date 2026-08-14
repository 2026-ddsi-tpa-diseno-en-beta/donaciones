package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import ar.edu.utn.dds.k3003.model.Identificador;
import ar.edu.utn.dds.k3003.model.TipoIdentificador;

/** Frontera entre el dominio y los DTOs de la catedra: aca se traduce el tipo de identificador. */
public class IdentificadorMapper {

  public Identificador toModel(IdentificadorDTO identificadorDTO) {
    return new Identificador(toModel(identificadorDTO.tipo()), identificadorDTO.descripcion());
  }

  public IdentificadorDTO toDTO(Identificador identificador) {
    return new IdentificadorDTO(
        identificador.getId(), toDTO(identificador.getTipo()), identificador.getDescripcion());
  }

  public TipoIdentificadorEnum toDTO(TipoIdentificador tipo) {
    if (tipo == null) {
      return null;
    }
    return switch (tipo) {
      case CODIGODEBARRAS -> TipoIdentificadorEnum.CODIGODEBARRAS;
      case QR -> TipoIdentificadorEnum.QR;
    };
  }

  public TipoIdentificador toModel(TipoIdentificadorEnum tipo) {
    if (tipo == null) {
      return null;
    }
    return switch (tipo) {
      case CODIGODEBARRAS -> TipoIdentificador.CODIGODEBARRAS;
      case QR -> TipoIdentificador.QR;
    };
  }
}

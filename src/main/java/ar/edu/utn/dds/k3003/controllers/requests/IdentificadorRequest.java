package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Body de alta y modificacion. El identificador numerico lo asigna el modulo. */
public record IdentificadorRequest(
    @NotNull TipoIdentificadorEnum tipo, @NotBlank String descripcion) {

  public IdentificadorDTO toDTO() {
    return new IdentificadorDTO(null, tipo, descripcion);
  }
}

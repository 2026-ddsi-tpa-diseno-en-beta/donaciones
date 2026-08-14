package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import jakarta.validation.constraints.NotBlank;

/** Actualiza los datos propios de una categoria sin modificar su lugar en la jerarquia. */
public record CategoriaUpdateRequest(
    @NotBlank String nombre, @NotBlank String descripcion) {

  public CategoriaDTO toDTO() {
    return new CategoriaDTO(null, nombre, descripcion, null);
  }
}

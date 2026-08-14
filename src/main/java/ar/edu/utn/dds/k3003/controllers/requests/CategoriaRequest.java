package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import jakarta.validation.constraints.NotBlank;

/**
 * Body de alta de categoria. {@code categoriaPadreID} es la forma natural de armar el arbol
 * ("creo 'fideos' colgando de 'Alimentos'"). El ID lo genera el modulo.
 */
public record CategoriaRequest(
    @NotBlank String nombre, @NotBlank String descripcion, String categoriaPadreID) {

  public CategoriaDTO toDTO() {
    return new CategoriaDTO(null, nombre, descripcion, categoriaPadreID);
  }
}

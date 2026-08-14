package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import jakarta.validation.constraints.NotBlank;

/**
 * Body de alta/modificacion de producto. Es el {@code ProductoDTO} de la catedra mas la
 * subcategoria opcional para categorias hoja, que no entra en el DTO base.
 */
public record ProductoRequest(
    @NotBlank String nombre,
    @NotBlank String descripcion,
    @NotBlank String categoriaID,
    String subcategoriaID,
    String identificadorID) {

  public ProductoDTO toDTO() {
    return new ProductoDTO(null, nombre, descripcion, categoriaID, identificadorID);
  }
}

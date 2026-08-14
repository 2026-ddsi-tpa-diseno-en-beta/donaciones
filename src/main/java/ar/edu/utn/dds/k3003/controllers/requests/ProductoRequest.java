package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;

/**
 * Body de alta/modificacion de producto. Es el {@code ProductoDTO} de la catedra mas la
 * subcategoria, que es la unidad minima de asignacion y no entra en el DTO base.
 */
public record ProductoRequest(
    String id,
    String nombre,
    String descripcion,
    String categoriaID,
    String subcategoriaID,
    String identificadorID) {

  public ProductoDTO toDTO() {
    return new ProductoDTO(id, nombre, descripcion, categoriaID, identificadorID);
  }
}

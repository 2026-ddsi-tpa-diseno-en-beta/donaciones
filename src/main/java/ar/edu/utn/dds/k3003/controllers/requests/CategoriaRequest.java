package ar.edu.utn.dds.k3003.controllers.requests;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;

/**
 * Body de alta/modificacion de categoria. {@code categoriaPadreID} es la forma natural de armar el
 * arbol ("creo 'fideos' colgando de 'Alimentos'"); {@code subcategoriaID} se mantiene por
 * compatibilidad con el DTO de la catedra.
 */
public record CategoriaRequest(
    String id, String nombre, String descripcion, String categoriaPadreID, String subcategoriaID) {

  public CategoriaDTO toDTO() {
    return new CategoriaDTO(id, nombre, descripcion, categoriaPadreID);
  }

  public String padreEfectivo() {
    if (categoriaPadreID != null && !categoriaPadreID.isBlank()) {
      return categoriaPadreID;
    }
    return subcategoriaID;
  }
}

package ar.edu.utn.dds.k3003.controllers.responses;

import ar.edu.utn.dds.k3003.model.Producto;

public record ProductoResponse(
    String id,
    String nombre,
    String descripcion,
    String categoriaID,
    String subcategoriaID,
    String identificadorID) {

  public static ProductoResponse desde(Producto producto) {
    return new ProductoResponse(
        producto.getId(),
        producto.getNombre(),
        producto.getDescripcion(),
        producto.getCategoriaId(),
        producto.getSubcategoriaId(),
        producto.getIdentificadorId());
  }
}

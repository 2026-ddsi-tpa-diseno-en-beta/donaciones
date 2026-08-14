package ar.edu.utn.dds.k3003.controllers.responses;

import ar.edu.utn.dds.k3003.model.Categoria;

/**
 * Respuesta propia del modulo. El {@code CategoriaDTO} de la catedra tiene un campo
 * {@code subcategoriaID} que modela una sola subcategoria; aca se expone el vinculo al reves
 * ({@code categoriaPadreID}), que es lo que permite que una categoria tenga N subcategorias.
 */
public record CategoriaResponse(
    String id,
    String nombre,
    String descripcion,
    String categoriaPadreID,
    boolean esSubcategoria) {

  public static CategoriaResponse desde(Categoria categoria) {
    return new CategoriaResponse(
        categoria.getId(),
        categoria.getNombre(),
        categoria.getDescripcion(),
        categoria.getCategoriaPadreId(),
        categoria.esSubcategoria());
  }
}

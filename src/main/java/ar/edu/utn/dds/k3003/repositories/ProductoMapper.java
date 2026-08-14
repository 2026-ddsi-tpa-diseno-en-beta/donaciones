package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.Identificador;
import ar.edu.utn.dds.k3003.model.Producto;

public class ProductoMapper {

  public Producto toModel(ProductoDTO productoDTO, String subcategoriaId, Identificador ident) {
    return new Producto(
        productoDTO.nombre(),
        productoDTO.descripcion(),
        productoDTO.categoriaID(),
        subcategoriaId,
        ident);
  }

  /**
   * El {@code ProductoDTO} de la catedra no tiene campo de subcategoria, por lo que ese dato solo
   * viaja por los endpoints propios del modulo (ver {@code ProductoResponse}).
   */
  public ProductoDTO toDTO(Producto producto) {
    return new ProductoDTO(
        producto.getId(),
        producto.getNombre(),
        producto.getDescripcion(),
        producto.getCategoriaId(),
        producto.getIdentificadorId());
  }
}

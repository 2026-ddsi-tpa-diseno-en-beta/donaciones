package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.Identificador;
import ar.edu.utn.dds.k3003.model.Producto;

public class ProductoMapper {

  public Producto toModel(ProductoDTO productoDTO, Identificador identificador) {
    return new Producto(
        productoDTO.nombre(),
        productoDTO.descripcion(),
        productoDTO.categoriaID(),
        identificador);
  }

  public ProductoDTO toDTO(Producto producto) {
    return new ProductoDTO(
        producto.getId(),
        producto.getNombre(),
        producto.getDescripcion(),
        producto.getCategoriaId(),
        producto.getIdentificadorId());
  }
}

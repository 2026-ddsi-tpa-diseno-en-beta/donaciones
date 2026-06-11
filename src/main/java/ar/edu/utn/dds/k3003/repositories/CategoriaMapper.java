package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.model.Categoria;

public class CategoriaMapper {

  public Categoria toModel(CategoriaDTO categoriaDTO) {
    return new Categoria(
        categoriaDTO.nombre(), categoriaDTO.descripcion(), categoriaDTO.subcategoriaID());
  }

  public CategoriaDTO toDTO(Categoria categoria) {
    return new CategoriaDTO(
        categoria.getId(),
        categoria.getNombre(),
        categoria.getDescripcion(),
        categoria.getSubcategoriaId());
  }
}

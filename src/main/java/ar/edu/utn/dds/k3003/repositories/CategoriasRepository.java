package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriasRepository {
  Categoria save(Categoria categoria);

  Optional<Categoria> findById(String id);

  List<Categoria> findAll();

  void deleteAll();
}

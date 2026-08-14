package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductosRepository {
  Producto save(Producto producto);

  Optional<Producto> findById(String id);

  List<Producto> buscarPorCategoria(String categoriaId);

  List<Producto> buscarPorIdentificador(String identificadorId);

  List<Producto> findAll();

  void deleteById(String id);

  void deleteAll();
}

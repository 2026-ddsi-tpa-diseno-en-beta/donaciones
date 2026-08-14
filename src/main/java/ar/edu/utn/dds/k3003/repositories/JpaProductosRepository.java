package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Producto;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataProductosRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProductosRepository implements ProductosRepository {
  private final SpringDataProductosRepository repository;

  public JpaProductosRepository(SpringDataProductosRepository repository) {
    this.repository = repository;
  }

  @Override
  public Producto save(Producto producto) {
    return repository.save(producto);
  }

  @Override
  public Optional<Producto> findById(String id) {
    return id == null ? Optional.empty() : repository.findById(id);
  }

  @Override
  public List<Producto> buscarPorCategoria(String categoriaId) {
    return repository.findByCategoriaOSubcategoria(categoriaId);
  }

  @Override
  public List<Producto> buscarPorIdentificador(String identificadorId) {
    return repository.findByIdentificadorId(identificadorId);
  }

  @Override
  public List<Producto> findAll() {
    return repository.findAll();
  }

  @Override
  public void deleteById(String id) {
    repository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }
}

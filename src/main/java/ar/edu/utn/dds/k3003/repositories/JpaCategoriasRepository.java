package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Categoria;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataCategoriasRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCategoriasRepository implements CategoriasRepository {
  private final SpringDataCategoriasRepository repository;

  public JpaCategoriasRepository(SpringDataCategoriasRepository repository) {
    this.repository = repository;
  }

  @Override
  public Categoria save(Categoria categoria) {
    return repository.save(categoria);
  }

  @Override
  public Optional<Categoria> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<Categoria> findAll() {
    return repository.findAll();
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }
}

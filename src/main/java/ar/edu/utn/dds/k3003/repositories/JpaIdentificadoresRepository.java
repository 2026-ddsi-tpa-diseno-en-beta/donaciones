package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Identificador;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataIdentificadoresRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaIdentificadoresRepository implements IdentificadoresRepository {
  private final SpringDataIdentificadoresRepository repository;

  public JpaIdentificadoresRepository(SpringDataIdentificadoresRepository repository) {
    this.repository = repository;
  }

  @Override
  public Identificador save(Identificador identificador) {
    return repository.save(identificador);
  }

  @Override
  public Optional<Identificador> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<Identificador> findAll() {
    return repository.findAll();
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }
}

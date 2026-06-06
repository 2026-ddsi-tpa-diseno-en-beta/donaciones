package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataDonacionesRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDonacionesRepository implements DonacionesRepository {
  private final SpringDataDonacionesRepository repository;

  public JpaDonacionesRepository(SpringDataDonacionesRepository repository) {
    this.repository = repository;
  }

  @Override
  public Donacion save(Donacion donacion) {
    return repository.save(donacion);
  }

  @Override
  public Optional<Donacion> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<Donacion> buscarPorDonador(String donadorId) {
    return repository.findByDonadorId(donadorId);
  }

  @Override
  public List<Donacion> findAll() {
    return repository.findAll();
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }
}

package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Secuencia;
import ar.edu.utn.dds.k3003.repositories.springdata.SpringDataSecuenciaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaGeneradorDeIds implements GeneradorDeIds {
  private final SpringDataSecuenciaRepository repository;

  public JpaGeneradorDeIds(SpringDataSecuenciaRepository repository) {
    this.repository = repository;
  }

  @Override
  public String siguiente(String secuencia) {
    Secuencia contador =
        repository.findByNombre(secuencia).orElseGet(() -> new Secuencia(secuencia));
    String id = String.valueOf(contador.siguiente());
    repository.save(contador);
    return id;
  }
}

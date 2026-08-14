package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Identificador;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryIdentificadoresRepo implements IdentificadoresRepository {
  private final Map<String, Identificador> identificadores = new LinkedHashMap<>();
  private final GeneradorDeIds generadorDeIds;

  public InMemoryIdentificadoresRepo() {
    this(new InMemoryGeneradorDeIds());
  }

  public InMemoryIdentificadoresRepo(GeneradorDeIds generadorDeIds) {
    this.generadorDeIds = generadorDeIds;
  }

  @Override
  public Identificador save(Identificador identificador) {
    if (identificador.getId() == null) {
      identificador.setId(generadorDeIds.siguiente("identificador"));
    }
    this.identificadores.put(identificador.getId(), identificador);
    return identificador;
  }

  @Override
  public Optional<Identificador> findById(String id) {
    return Optional.ofNullable(this.identificadores.get(id));
  }

  @Override
  public List<Identificador> findAll() {
    return new ArrayList<>(this.identificadores.values());
  }

  @Override
  public void deleteById(String id) {
    this.identificadores.remove(id);
  }

  @Override
  public void deleteAll() {
    this.identificadores.clear();
  }
}

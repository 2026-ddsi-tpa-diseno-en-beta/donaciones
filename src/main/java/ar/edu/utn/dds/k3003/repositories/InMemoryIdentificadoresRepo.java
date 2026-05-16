package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Identificador;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryIdentificadoresRepo implements IdentificadoresRepository {
  private final List<Identificador> identificadores;
  private Integer idSecuencial;

  public InMemoryIdentificadoresRepo() {
    this.identificadores = new ArrayList<>();
    this.idSecuencial = 1;
  }

  @Override
  public Identificador save(Identificador identificador) {
    if (identificador.getId() == null) {
      identificador.setId(String.valueOf(idSecuencial));
      idSecuencial++;
      this.identificadores.add(identificador);
    }

    return identificador;
  }

  @Override
  public Optional<Identificador> findById(String id) {
    return this.identificadores.stream().filter(i -> i.getId().equals(id)).findFirst();
  }
}

package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Categoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryCategoriasRepo implements CategoriasRepository {
  private final List<Categoria> categorias;
  private Integer idSecuencial;

  public InMemoryCategoriasRepo() {
    this.categorias = new ArrayList<>();
    this.idSecuencial = 1;
  }

  @Override
  public Categoria save(Categoria categoria) {
    if (categoria.getId() == null) {
      categoria.setId(String.valueOf(idSecuencial));
      idSecuencial++;
      this.categorias.add(categoria);
    }

    return categoria;
  }

  @Override
  public Optional<Categoria> findById(String id) {
    return this.categorias.stream().filter(c -> c.getId().equals(id)).findFirst();
  }

  @Override
  public List<Categoria> findAll() {
    return new ArrayList<>(this.categorias);
  }

  @Override
  public void deleteAll() {
    this.categorias.clear();
    this.idSecuencial = 1;
  }
}

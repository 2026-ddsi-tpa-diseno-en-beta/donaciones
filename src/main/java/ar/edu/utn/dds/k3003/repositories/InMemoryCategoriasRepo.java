package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Categoria;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCategoriasRepo implements CategoriasRepository {
  private final Map<String, Categoria> categorias = new LinkedHashMap<>();
  private final GeneradorDeIds generadorDeIds;

  public InMemoryCategoriasRepo() {
    this(new InMemoryGeneradorDeIds());
  }

  public InMemoryCategoriasRepo(GeneradorDeIds generadorDeIds) {
    this.generadorDeIds = generadorDeIds;
  }

  @Override
  public Categoria save(Categoria categoria) {
    if (categoria.getId() == null) {
      categoria.setId(generadorDeIds.siguiente("categoria"));
    }
    this.categorias.put(categoria.getId(), categoria);
    return categoria;
  }

  @Override
  public Optional<Categoria> findById(String id) {
    return Optional.ofNullable(this.categorias.get(id));
  }

  @Override
  public List<Categoria> buscarSubcategoriasDe(String categoriaPadreId) {
    return this.categorias.values().stream()
        .filter(c -> Objects.equals(c.getCategoriaPadreId(), categoriaPadreId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Categoria> findAll() {
    return new ArrayList<>(this.categorias.values());
  }

  @Override
  public void deleteById(String id) {
    this.categorias.remove(id);
  }

  @Override
  public void deleteAll() {
    this.categorias.clear();
  }
}

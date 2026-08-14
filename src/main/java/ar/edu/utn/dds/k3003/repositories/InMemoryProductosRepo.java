package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Producto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryProductosRepo implements ProductosRepository {
  private final Map<String, Producto> productos = new LinkedHashMap<>();
  private final GeneradorDeIds generadorDeIds;

  public InMemoryProductosRepo() {
    this(new InMemoryGeneradorDeIds());
  }

  public InMemoryProductosRepo(GeneradorDeIds generadorDeIds) {
    this.generadorDeIds = generadorDeIds;
  }

  @Override
  public Producto save(Producto producto) {
    if (producto.getId() == null) {
      producto.setId(generadorDeIds.siguiente("producto"));
    }
    this.productos.put(producto.getId(), producto);
    return producto;
  }

  @Override
  public Optional<Producto> findById(String id) {
    return Optional.ofNullable(this.productos.get(id));
  }

  @Override
  public List<Producto> buscarPorCategoria(String categoriaId) {
    return this.productos.values().stream()
        .filter(p -> Objects.equals(p.getCategoriaId(), categoriaId)
            || Objects.equals(p.getSubcategoriaId(), categoriaId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Producto> buscarPorIdentificador(String identificadorId) {
    return this.productos.values().stream()
        .filter(p -> Objects.equals(p.getIdentificadorId(), identificadorId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Producto> findAll() {
    return new ArrayList<>(this.productos.values());
  }

  @Override
  public void deleteById(String id) {
    this.productos.remove(id);
  }

  @Override
  public void deleteAll() {
    this.productos.clear();
  }
}

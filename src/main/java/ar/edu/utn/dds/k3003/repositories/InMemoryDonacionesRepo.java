package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donacion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryDonacionesRepo implements DonacionesRepository {
  private final Map<String, Donacion> donaciones = new LinkedHashMap<>();
  private final GeneradorDeIds generadorDeIds;

  public InMemoryDonacionesRepo() {
    this(new InMemoryGeneradorDeIds());
  }

  public InMemoryDonacionesRepo(GeneradorDeIds generadorDeIds) {
    this.generadorDeIds = generadorDeIds;
  }

  @Override
  public Donacion save(Donacion donacion) {
    if (donacion.getId() == null) {
      donacion.setId(generadorDeIds.siguiente("donacion"));
    }
    this.donaciones.put(donacion.getId(), donacion);
    return donacion;
  }

  @Override
  public Optional<Donacion> findById(String id) {
    return Optional.ofNullable(this.donaciones.get(id));
  }

  @Override
  public List<Donacion> buscarPorDonador(String donadorId) {
    return this.donaciones.values().stream()
        .filter(d -> d.getDonadorId().equals(donadorId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Donacion> buscarPorProducto(String productoId) {
    return this.donaciones.values().stream()
        .filter(d -> d.getProductoId().equals(productoId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Donacion> findAll() {
    return new ArrayList<>(this.donaciones.values());
  }

  @Override
  public void deleteAll() {
    this.donaciones.clear();
  }
}

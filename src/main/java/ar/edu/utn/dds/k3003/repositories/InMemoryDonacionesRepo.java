package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donacion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryDonacionesRepo implements DonacionesRepository {
  private List<Donacion> donaciones;
  private Integer idSecuencial;

  public InMemoryDonacionesRepo() {
    this.donaciones = new ArrayList<>();
    this.idSecuencial = 1;
  }

  @Override
  public Donacion save(Donacion donacion) {
    if (donacion.getId() == null) {
      donacion.setId(String.valueOf(idSecuencial));
      idSecuencial++;

      this.donaciones.add(donacion);
    }
    return donacion;
  }

  @Override
  public Optional<Donacion> findById(String id) {
    return this.donaciones.stream().filter(d -> d.getId().equals(id)).findFirst();
  }

  @Override
  public List<Donacion> buscarPorDonador(String donadorId) {
    return this.donaciones.stream()
        .filter(d -> d.getDonadorId().equals(donadorId))
        .collect(Collectors.toList());
  }
}

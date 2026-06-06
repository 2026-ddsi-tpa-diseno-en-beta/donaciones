package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProductosRepo implements ProductosRepository {
  private final List<Producto> productos;
  private Integer idSecuencial;

  public InMemoryProductosRepo() {
    this.productos = new ArrayList<>();
    this.idSecuencial = 1;
  }

  @Override
  public Producto save(Producto producto) {
    if (producto.getId() == null) {
      producto.setId(String.valueOf(idSecuencial));
      idSecuencial++;
      this.productos.add(producto);
    }

    return producto;
  }

  @Override
  public Optional<Producto> findById(String id) {
    return this.productos.stream().filter(p -> p.getId().equals(id)).findFirst();
  }

  @Override
  public List<Producto> findAll() {
    return new ArrayList<>(this.productos);
  }

  @Override
  public void deleteAll() {
    this.productos.clear();
    this.idSecuencial = 1;
  }
}

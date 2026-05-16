package ar.edu.utn.dds.k3003.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Producto {
  private String id;
  private String nombre;
  private String descripcion;
  private String categoriaId;
  private String identificadorId;

  public Producto(
      String nombre, String descripcion, String categoriaId, Identificador identificador) {
    if (nombre == null || nombre.isBlank()) {
      throw new RuntimeException("El nombre del producto no puede ser nulo");
    }
    if (descripcion == null || descripcion.isBlank()) {
      throw new RuntimeException("La descripcion del producto no puede ser nula");
    }
    if (identificador == null) {
      throw new RuntimeException("El producto debe tener un identificador existente");
    }
    if (!identificador.esValidoPara(nombre, descripcion)) {
      throw new RuntimeException("El producto no cumple la validacion de su identificador");
    }

    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoriaId = categoriaId;
    this.identificadorId = identificador.getId();
  }
}

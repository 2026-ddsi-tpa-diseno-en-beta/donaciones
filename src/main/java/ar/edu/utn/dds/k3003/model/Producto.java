package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Producto {
  @Id @Setter private String id;

  private String nombre;
  private String descripcion;
  private String categoriaId;

  /**
   * Subcategoria a la que pertenece el producto. Es la unidad minima de asignacion del sistema: dos
   * productos de la misma categoria pero distinta subcategoria no son intercambiables.
   */
  private String subcategoriaId;

  /** Opcional: un producto puede no tener codigo de barras ni QR. */
  private String identificadorId;

  protected Producto() {}

  public Producto(
      String nombre,
      String descripcion,
      String categoriaId,
      String subcategoriaId,
      Identificador identificador) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre del producto no puede ser nulo");
    }
    if (descripcion == null || descripcion.isBlank()) {
      throw new IllegalArgumentException("La descripcion del producto no puede ser nula");
    }
    validarContra(identificador, nombre, descripcion);

    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoriaId = categoriaId;
    this.subcategoriaId = subcategoriaId;
    this.identificadorId = identificador == null ? null : identificador.getId();
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }

  /** Al modificar hay que revalidar: cambiar el nombre puede invalidar un identificador QR. */
  public void modificar(
      String nombre,
      String descripcion,
      String categoriaId,
      String subcategoriaId,
      Identificador identificador) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre del producto no puede ser nulo");
    }
    if (descripcion == null || descripcion.isBlank()) {
      throw new IllegalArgumentException("La descripcion del producto no puede ser nula");
    }
    validarContra(identificador, nombre, descripcion);

    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoriaId = categoriaId;
    this.subcategoriaId = subcategoriaId;
    this.identificadorId = identificador == null ? null : identificador.getId();
  }

  private static void validarContra(
      Identificador identificador, String nombre, String descripcion) {
    if (identificador != null && !identificador.esValidoPara(nombre, descripcion)) {
      throw new IllegalArgumentException(
          "El producto no cumple la validacion de su identificador ("
              + identificador.getTipo()
              + ")");
    }
  }
}

package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Categoria {
  @Id
  private String id;

  private String nombre;
  private String descripcion;
  private String subcategoriaId;

  protected Categoria() {}

  public Categoria(String nombre, String descripcion, String subcategoriaId) {
    if (nombre == null || nombre.isBlank()) {
      throw new RuntimeException("El nombre de la categoria no puede ser nulo");
    }
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.subcategoriaId = subcategoriaId;
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }
}

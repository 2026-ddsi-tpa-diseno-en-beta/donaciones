package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Categoria de productos. La jerarquia es de un solo nivel: una categoria raiz (Alimentos,
 * Vestimenta) agrupa subcategorias (fideos, arroz, legumbres), y la subcategoria es la unidad
 * minima de asignacion del sistema.
 *
 * <p>La relacion se modela con un unico mecanismo, {@code categoriaPadreId}, que permite N
 * subcategorias por categoria.
 */
@Entity
@Getter
public class Categoria {
  @Id @Setter private String id;

  private String nombre;
  private String descripcion;

  @Column(name = "categoria_padre_id")
  private String categoriaPadreId;

  protected Categoria() {}

  public Categoria(String nombre, String descripcion, String categoriaPadreId) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de la categoria no puede ser nulo");
    }
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoriaPadreId = categoriaPadreId;
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }

  public void modificar(String nombre, String descripcion) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre de la categoria no puede ser nulo");
    }
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  /** Cuelga esta categoria de otra, convirtiendola en subcategoria de aquella. */
  public void colgarDe(Categoria padre) {
    if (padre != null && padre.getId().equals(this.id)) {
      throw new IllegalArgumentException("Una categoria no puede ser subcategoria de si misma");
    }
    if (padre != null && padre.esSubcategoria()) {
      throw new IllegalArgumentException(
          "La jerarquia de categorias es de un solo nivel: '"
              + padre.getNombre()
              + "' ya es una subcategoria");
    }
    this.categoriaPadreId = padre == null ? null : padre.getId();
  }

  public boolean esSubcategoria() {
    return this.categoriaPadreId != null;
  }
}

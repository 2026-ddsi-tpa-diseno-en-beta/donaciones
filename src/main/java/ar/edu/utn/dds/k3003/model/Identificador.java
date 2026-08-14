package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Identificador {
  @Id @Setter private String id;

  @Enumerated(EnumType.STRING)
  private TipoIdentificador tipo;

  private String descripcion;

  protected Identificador() {}

  public Identificador(TipoIdentificador tipo, String descripcion) {
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de identificador no puede ser nulo");
    }

    this.tipo = tipo;
    this.descripcion = descripcion;
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }

  public void modificar(TipoIdentificador tipo, String descripcion) {
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de identificador no puede ser nulo");
    }
    this.tipo = tipo;
    this.descripcion = descripcion;
  }

  /** Delega la regla en el tipo: cada tipo sabe como validarse (polimorfismo, sin ifs). */
  public boolean esValidoPara(String nombreProducto, String descripcionProducto) {
    return this.tipo.esValidoPara(nombreProducto, descripcionProducto);
  }
}

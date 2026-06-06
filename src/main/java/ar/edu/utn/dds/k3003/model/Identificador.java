package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
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
@Setter
public class Identificador {
  @Id
  private String id;

  @Enumerated(EnumType.STRING)
  private TipoIdentificadorEnum tipo;

  private String descripcion;

  protected Identificador() {}

  public Identificador(TipoIdentificadorEnum tipo, String descripcion) {
    if (tipo == null) {
      throw new RuntimeException("El tipo de identificador no puede ser nulo");
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

  public Boolean esValidoPara(String nombreProducto, String descripcionProducto) {
    if (TipoIdentificadorEnum.CODIGODEBARRAS.equals(this.tipo)) {
      return cantidadDePalabras(descripcionProducto) >= 3;
    }

    if (TipoIdentificadorEnum.QR.equals(this.tipo)) {
      return cantidadDeLetras(nombreProducto) % 2 == 0;
    }

    return Boolean.FALSE;
  }

  private Integer cantidadDePalabras(String texto) {
    if (texto == null || texto.isBlank()) {
      return 0;
    }
    return texto.trim().split("\\s+").length;
  }

  private Integer cantidadDeLetras(String texto) {
    if (texto == null || texto.isBlank()) {
      return -1;
    }
    return (int) texto.chars().filter(Character::isLetter).count();
  }
}

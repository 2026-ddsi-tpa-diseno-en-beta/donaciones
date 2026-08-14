package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

/**
 * Contador persistido por tipo de entidad. Existe para poder cumplir con el enunciado: "cada
 * entidad debe tener un identificador numerico entidadID, su asignacion la maneja el componente
 * duenio".
 */
@Entity
@Getter
public class Secuencia {
  @Id private String nombre;

  private Long valor;

  protected Secuencia() {}

  public Secuencia(String nombre) {
    this.nombre = nombre;
    this.valor = 0L;
  }

  public Long siguiente() {
    this.valor = this.valor + 1;
    return this.valor;
  }

  /** Adelanta el contador para no pisar IDs que ya existan en la base. */
  public void adelantarHasta(Long piso) {
    if (piso != null && piso > this.valor) {
      this.valor = piso;
    }
  }
}

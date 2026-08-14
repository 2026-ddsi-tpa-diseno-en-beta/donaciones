package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Donacion {
  /** Lo asigna el componente duenio. El setter existe solo para que lo asignen los repositorios. */
  @Id @Setter private String id;

  private String donadorId;
  private String depositoId;
  private String descripcion;
  private String productoId;
  private Integer cantidad;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "donacion_historial_estados",
      joinColumns = @JoinColumn(name = "donacion_id"))
  @OrderColumn(name = "orden")
  private List<CambioEstadoDonacion> historialEstados;

  protected Donacion() {}

  public Donacion(
      String donadorId,
      String depositoId,
      String descripcion,
      String productoId,
      Integer cantidad) {
    if (donadorId == null || donadorId.isBlank()) {
      throw new IllegalArgumentException("La donacion debe indicar el donador");
    }
    if (productoId == null || productoId.isBlank()) {
      throw new IllegalArgumentException("La donacion debe indicar el producto donado");
    }
    if (cantidad == null || cantidad <= 0) {
      throw new IllegalArgumentException("La cantidad donada debe ser mayor a cero");
    }

    this.donadorId = donadorId;
    this.depositoId = depositoId;
    this.descripcion = descripcion;
    this.productoId = productoId;
    this.cantidad = cantidad;
    this.historialEstados = new ArrayList<>();
    this.agregarCambioDeEstado(EstadoDonacion.INGRESADA, "Donacion recien ingresada");
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }

  private void agregarCambioDeEstado(EstadoDonacion nuevoEstado, String detalle) {
    this.historialEstados.add(new CambioEstadoDonacion(nuevoEstado, detalle));
  }

  /**
   * Unico camino para mutar el estado. Valida la transicion contra la maquina de estados y deja
   * registro en el historial para garantizar la trazabilidad y auditoria.
   */
  public void cambiarEstado(EstadoDonacion nuevoEstado, String detalle) {
    if (nuevoEstado == null) {
      throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
    }
    if (!getEstadoActual().permiteTransicionA(nuevoEstado)) {
      throw new IllegalArgumentException(
          "Transicion de estado invalida: " + getEstadoActual() + " -> " + nuevoEstado);
    }

    this.agregarCambioDeEstado(nuevoEstado, detalle);
  }

  public EstadoDonacion getEstadoActual() {
    return historialEstados.get(historialEstados.size() - 1).getEstado();
  }

  /** El historial es de solo lectura hacia afuera: no se puede alterar la auditoria. */
  public List<CambioEstadoDonacion> getHistorialEstados() {
    return Collections.unmodifiableList(historialEstados);
  }

  public LocalDate getFechaIngreso() {
    return historialEstados.get(0).getFechaCambio().toLocalDate();
  }
}

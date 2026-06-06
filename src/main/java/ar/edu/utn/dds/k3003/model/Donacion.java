package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Donacion {
  @Id
  private String id;

  private String donadorId;
  private String depositoId;
  private String descripcion;
  private String productoId;
  private Integer cantidad;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "donacion_historial_estados", joinColumns = @JoinColumn(name = "donacion_id"))
  @OrderColumn(name = "orden")
  private List<CambioEstadoDonacion> historialEstados;

  protected Donacion() {}

  public Donacion(
      String donadorId,
      String depositoId,
      String descripcion,
      String productoId,
      Integer cantidad) {
    this.donadorId = donadorId;
    this.depositoId = depositoId;
    this.descripcion = descripcion;
    this.productoId = productoId;
    this.cantidad = cantidad;
    this.historialEstados = new ArrayList<>();
    this.agregarCambioDeEstado(EstadoDonacionEnum.INGRESADA, "Donacion recien ingresada");
  }

  @PrePersist
  private void asignarIdSiHaceFalta() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
    }
  }

  public void agregarCambioDeEstado(EstadoDonacionEnum nuevoEstado, String detalle) {
    this.historialEstados.add(new CambioEstadoDonacion(nuevoEstado, detalle));
  }

  public void cambiarEstado(EstadoDonacionEnum nuevoEstado, String detalle) {
    if (nuevoEstado == null) {
      throw new RuntimeException("El nuevo estado no puede ser nulo");
    }
    if (!puedeCambiarA(nuevoEstado)) {
      throw new RuntimeException("La transicion de estado solicitada no es valida");
    }

    this.agregarCambioDeEstado(nuevoEstado, detalle);
  }

  public EstadoDonacionEnum getEstadoActual() {
    return historialEstados.get(historialEstados.size() - 1).getEstado();
  }

  private Boolean puedeCambiarA(EstadoDonacionEnum nuevoEstado) {
    EstadoDonacionEnum estadoActual = this.getEstadoActual();

    if (EstadoDonacionEnum.ACEPTADA.equals(nuevoEstado)) {
      return EstadoDonacionEnum.INGRESADA.equals(estadoActual);
    }
    if (EstadoDonacionEnum.CONQUEJA.equals(nuevoEstado)) {
      return EstadoDonacionEnum.ACEPTADA.equals(estadoActual);
    }

    return Boolean.FALSE;
  }
}

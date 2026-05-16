package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Donacion {
  private String id;
  private String donadorId;
  private String depositoId;
  private String descripcion;
  private String productoId;
  private Integer cantidad;
  private List<CambioEstadoDonacion> historialEstados;

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

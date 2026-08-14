package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.controllers.responses.CambioEstadoResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donaciones")
public class DonacionesController {

  private final Fachada fachada;

  public DonacionesController(Fachada fachada) {
    this.fachada = fachada;
  }

  @PostMapping
  @ApiResponse(responseCode = "201", description = "Donacion registrada")
  public ResponseEntity<DonacionDTO> registrarDonacion(@RequestBody DonacionDTO donacionDTO) {
    if (donacionDTO != null && donacionDTO.id() != null && !donacionDTO.id().isBlank()) {
      throw new IllegalArgumentException("El ID de la donacion lo genera el modulo");
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(fachada.registrarDonacion(donacionDTO));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DonacionDTO> buscarDonacionPorID(@PathVariable("id") String donacionID) {
    return ResponseEntity.ok(fachada.buscarDonacionPorID(donacionID));
  }

  @GetMapping
  public ResponseEntity<List<DonacionDTO>> listarOBuscarPorDonadorYFechaInicio(
      @RequestParam(required = false) String donadorID,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate fecha) {
    if (donadorID == null && fecha == null) {
      return ResponseEntity.ok(fachada.listarDonaciones());
    }
    if (donadorID == null || fecha == null) {
      throw new IllegalArgumentException("Debe indicar donadorID y fecha para filtrar donaciones");
    }
    return ResponseEntity.ok(fachada.buscarPorDonadorYFechaInicio(donadorID, fecha));
  }

  /**
   * Historial completo de cambios de estado de una donacion. Cubre el requisito de trazabilidad y
   * auditoria sin necesidad de mirar la base de datos.
   */
  @GetMapping("/{id}/historial")
  public ResponseEntity<List<CambioEstadoResponse>> historialDeEstados(
      @PathVariable("id") String donacionID) {
    return ResponseEntity.ok(
        fachada.obtenerHistorialDeEstados(donacionID).stream()
            .map(CambioEstadoResponse::desde)
            .toList());
  }

  @PatchMapping("/{id}/estado")
  public ResponseEntity<DonacionDTO> cambiarEstado(
      @PathVariable("id") String donacionID, @RequestParam EstadoDonacionEnum estado) {
    if (!EstadoDonacionEnum.ACEPTADA.equals(estado)) {
      throw new IllegalArgumentException(
          "Este endpoint solo permite confirmar la aceptacion desde Logistica");
    }
    return ResponseEntity.ok(fachada.cambiarEstadoDeDonacion(donacionID, estado));
  }

  @PostMapping("/{id}/quejas")
  public ResponseEntity<DonacionDTO> registrarQueja(
      @PathVariable("id") String donacionID, @Valid @RequestBody QuejaRequest quejaRequest) {
    return ResponseEntity.ok(
        fachada.registrarQuejaEnDonacion(donacionID, quejaRequest.descripcion()));
  }

  public record QuejaRequest(@NotBlank String descripcion) {}
}

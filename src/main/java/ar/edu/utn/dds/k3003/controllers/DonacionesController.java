package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
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
  public ResponseEntity<DonacionDTO> registrarDonacion(@RequestBody DonacionDTO donacionDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(fachada.registrarDonacion(donacionDTO));
  }

  @GetMapping("/{donacionID}")
  public ResponseEntity<DonacionDTO> buscarDonacionPorID(@PathVariable String donacionID) {
    return ResponseEntity.ok(fachada.buscarDonacionPorID(donacionID));
  }

  @GetMapping
  public ResponseEntity<List<DonacionDTO>> buscarPorDonadorYFechaInicio(
      @RequestParam String donadorID,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
    return ResponseEntity.ok(fachada.buscarPorDonadorYFechaInicio(donadorID, fecha));
  }

  @PatchMapping("/{donacionID}/estado")
  public ResponseEntity<DonacionDTO> cambiarEstado(
      @PathVariable String donacionID, @RequestParam EstadoDonacionEnum estado) {
    return ResponseEntity.ok(fachada.cambiarEstadoDeDonacion(donacionID, estado));
  }

  @PostMapping("/{donacionID}/quejas")
  public ResponseEntity<DonacionDTO> registrarQueja(
      @PathVariable String donacionID, @RequestBody QuejaRequest quejaRequest) {
    return ResponseEntity.ok(
        fachada.registrarQuejaEnDonacion(donacionID, quejaRequest.descripcion()));
  }

  public record QuejaRequest(String descripcion) {}
}

package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.config.EstadoIntegraciones;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
  private final Fachada fachada;
  private final EstadoIntegraciones estadoIntegraciones;

  public AdminController(Fachada fachada, EstadoIntegraciones estadoIntegraciones) {
    this.fachada = fachada;
    this.estadoIntegraciones = estadoIntegraciones;
  }

  @DeleteMapping("/datos")
  public ResponseEntity<Void> limpiarDatos() {
    fachada.limpiarDatos();
    return ResponseEntity.noContent().build();
  }

  /** Estado del modulo sin necesidad de mirar la base de datos ni los logs. */
  @GetMapping("/estado")
  public ResponseEntity<Map<String, Object>> estado() {
    return ResponseEntity.ok(
        Map.of(
            "donaciones", fachada.listarDonaciones().size(),
            "productos", fachada.listarProductos().size(),
            "categorias", fachada.listarCategorias().size(),
            "identificadores", fachada.listarIdentificadores().size(),
            "integraciones", estadoIntegraciones.componentes(),
            "integracionesReales", estadoIntegraciones.todasReales()));
  }
}

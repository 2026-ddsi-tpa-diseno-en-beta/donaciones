package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
  private final Fachada fachada;

  public AdminController(Fachada fachada) {
    this.fachada = fachada;
  }

  @DeleteMapping("/datos")
  public ResponseEntity<Void> limpiarDatos() {
    fachada.limpiarDatos();
    return ResponseEntity.noContent().build();
  }
}

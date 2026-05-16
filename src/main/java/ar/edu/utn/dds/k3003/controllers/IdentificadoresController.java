package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identificadores")
public class IdentificadoresController {

  private final Fachada fachada;

  public IdentificadoresController(Fachada fachada) {
    this.fachada = fachada;
  }

  @PostMapping
  public ResponseEntity<IdentificadorDTO> agregarIdentificador(
      @RequestBody IdentificadorDTO identificadorDTO) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fachada.agregarIdentificador(identificadorDTO));
  }

  @GetMapping("/{identificadorID}")
  public ResponseEntity<IdentificadorDTO> buscarIdentificadorPorID(
      @PathVariable String identificadorID) {
    return ResponseEntity.ok(fachada.buscarIdentificadorPorID(identificadorID));
  }
}

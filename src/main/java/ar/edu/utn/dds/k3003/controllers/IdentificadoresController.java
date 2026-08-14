package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.controllers.requests.IdentificadorRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  @ApiResponse(responseCode = "201", description = "Identificador creado")
  public ResponseEntity<IdentificadorDTO> agregarIdentificador(
      @Valid @RequestBody IdentificadorRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fachada.agregarIdentificador(request.toDTO()));
  }

  @GetMapping
  public ResponseEntity<List<IdentificadorDTO>> listarIdentificadores() {
    return ResponseEntity.ok(fachada.listarIdentificadores());
  }

  @GetMapping("/{id}")
  public ResponseEntity<IdentificadorDTO> buscarIdentificadorPorID(@PathVariable("id") String id) {
    return ResponseEntity.ok(fachada.buscarIdentificadorPorID(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<IdentificadorDTO> modificarIdentificador(
      @PathVariable("id") String id, @Valid @RequestBody IdentificadorRequest request) {
    return ResponseEntity.ok(fachada.modificarIdentificador(id, request.toDTO()));
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204", description = "Identificador eliminado")
  public ResponseEntity<Void> eliminarIdentificador(@PathVariable("id") String id) {
    fachada.eliminarIdentificador(id);
    return ResponseEntity.noContent().build();
  }
}

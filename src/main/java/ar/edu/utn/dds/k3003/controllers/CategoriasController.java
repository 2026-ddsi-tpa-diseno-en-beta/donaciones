package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.controllers.requests.CategoriaRequest;
import ar.edu.utn.dds.k3003.controllers.responses.CategoriaResponse;
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
@RequestMapping("/categorias")
public class CategoriasController {

  private final Fachada fachada;

  public CategoriasController(Fachada fachada) {
    this.fachada = fachada;
  }

  @PostMapping
  public ResponseEntity<CategoriaResponse> agregarCategoria(@RequestBody CategoriaRequest request) {
    String id = fachada.agregarCategoria(request.toDTO(), request.padreEfectivo()).id();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CategoriaResponse.desde(fachada.verCategoria(id)));
  }

  @GetMapping
  public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
    return ResponseEntity.ok(
        fachada.listarCategoriasDelDominio().stream().map(CategoriaResponse::desde).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoriaResponse> buscarCategoriaPorID(@PathVariable("id") String id) {
    return ResponseEntity.ok(CategoriaResponse.desde(fachada.verCategoria(id)));
  }

  /** Subcategorias que componen esta categoria: las unidades minimas de asignacion. */
  @GetMapping("/{id}/subcategorias")
  public ResponseEntity<List<CategoriaResponse>> listarSubcategorias(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        fachada.listarSubcategoriasDelDominio(id).stream().map(CategoriaResponse::desde).toList());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoriaResponse> modificarCategoria(
      @PathVariable("id") String id, @RequestBody CategoriaRequest request) {
    fachada.modificarCategoria(id, request.toDTO());
    return ResponseEntity.ok(CategoriaResponse.desde(fachada.verCategoria(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarCategoria(@PathVariable("id") String id) {
    fachada.eliminarCategoria(id);
    return ResponseEntity.noContent().build();
  }
}

package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<CategoriaDTO> agregarCategoria(@RequestBody CategoriaDTO categoriaDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarCategoria(categoriaDTO));
  }

  @GetMapping
  public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
    return ResponseEntity.ok(fachada.listarCategorias());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoriaDTO> buscarCategoriaPorID(
      @PathVariable("id") String categoriaID) {
    return ResponseEntity.ok(fachada.buscarCategoriaPorID(categoriaID));
  }
}

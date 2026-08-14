package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.controllers.requests.ProductoRequest;
import ar.edu.utn.dds.k3003.controllers.responses.ProductoResponse;
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
@RequestMapping("/productos")
public class ProductosController {

  private final Fachada fachada;

  public ProductosController(Fachada fachada) {
    this.fachada = fachada;
  }

  @PostMapping
  public ResponseEntity<ProductoResponse> agregarProducto(@RequestBody ProductoRequest request) {
    ProductoDTO creado = fachada.agregarProducto(request.toDTO(), request.subcategoriaID());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ProductoResponse.desde(fachada.verProducto(creado.id())));
  }

  @GetMapping
  public ResponseEntity<List<ProductoDTO>> listarProductos() {
    return ResponseEntity.ok(fachada.listarProductos());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductoResponse> buscarProductoPorID(@PathVariable("id") String id) {
    return ResponseEntity.ok(ProductoResponse.desde(fachada.verProducto(id)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductoResponse> modificarProducto(
      @PathVariable("id") String id, @RequestBody ProductoRequest request) {
    fachada.modificarProducto(id, request.toDTO(), request.subcategoriaID());
    return ResponseEntity.ok(ProductoResponse.desde(fachada.verProducto(id)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarProducto(@PathVariable("id") String id) {
    fachada.eliminarProducto(id);
    return ResponseEntity.noContent().build();
  }
}

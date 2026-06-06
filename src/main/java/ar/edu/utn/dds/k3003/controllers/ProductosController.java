package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
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
@RequestMapping("/productos")
public class ProductosController {

  private final Fachada fachada;

  public ProductosController(Fachada fachada) {
    this.fachada = fachada;
  }

  @PostMapping
  public ResponseEntity<ProductoDTO> agregarProducto(@RequestBody ProductoDTO productoDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarProducto(productoDTO));
  }

  @GetMapping
  public ResponseEntity<List<ProductoDTO>> listarProductos() {
    return ResponseEntity.ok(fachada.listarProductos());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductoDTO> buscarProductoPorID(@PathVariable("id") String productoID) {
    return ResponseEntity.ok(fachada.buscarProductoPorID(productoID));
  }
}

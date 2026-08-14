package ar.edu.utn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.DonacionRechazadaException;
import ar.edu.utn.dds.k3003.exceptions.RecursoEnUsoException;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Cubre lo agregado para cerrar los pendientes de las entregas 1 a 4. */
class FachadaDonacionesAbmTest {

  private Fachada fachada;
  private FachadaDonadoresYEntidades donadores;

  @BeforeEach
  void setUp() {
    fachada = new Fachada();
    donadores = mock(FachadaDonadoresYEntidades.class);
    fachada.setFachadaDonadoresYEntidades(donadores);
    fachada.setFachadaLogistica(mock(FachadaLogistica.class));

    when(donadores.buscarDonadorPorID("donador1"))
        .thenReturn(
            new DonadorDTO("donador1", "N", "A", 30, "m@m.com", "1", "dom", null, null));
    when(donadores.puedeDonar("donador1")).thenReturn(Boolean.TRUE);
  }

  private CategoriaDTO categoria(String nombre) {
    return fachada.agregarCategoria(new CategoriaDTO(null, nombre, "desc", null));
  }

  private CategoriaDTO subcategoria(String nombre, String padreId) {
    return fachada.agregarCategoria(new CategoriaDTO(null, nombre, "desc", null), padreId);
  }

  private IdentificadorDTO identificador(TipoIdentificadorEnum tipo) {
    return fachada.agregarIdentificador(new IdentificadorDTO(null, tipo, "ident"));
  }

  // ------------------------------------------------- identificador opcional

  @Test
  @DisplayName("El identificador es opcional: se puede crear un producto sin codigo de barras ni QR")
  void productoSinIdentificador() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));

    assertNotNull(producto.id());
    assertNull(producto.identificadorID());
  }

  @Test
  @DisplayName("Con identificador, la validacion se sigue aplicando")
  void productoConIdentificadorInvalido() {
    String ident = identificador(TipoIdentificadorEnum.QR).id();

    // "Silla" tiene 5 letras: impar, invalido para QR.
    assertThrows(
        IllegalArgumentException.class,
        () -> fachada.agregarProducto(new ProductoDTO(null, "Silla", "una silla", null, ident)));
  }

  @Test
  @DisplayName("Un identificador inexistente sigue siendo un error, no se ignora")
  void productoConIdentificadorInexistente() {
    assertThrows(
        NoSuchElementException.class,
        () -> fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, "no-existe")));
  }

  // -------------------------------------------------------- subcategorias

  @Test
  @DisplayName("La subcategoria es la unidad minima de asignacion del producto")
  void productoSeClasificaPorSubcategoria() {
    CategoriaDTO alimentos = categoria("Alimentos");
    CategoriaDTO fideos = subcategoria("Fideos", alimentos.id());

    fachada.agregarProducto(
        new ProductoDTO(null, "Fideos", "medio kilo de fideos", alimentos.id(), null), fideos.id());

    Producto guardado = fachada.verProducto(fachada.listarProductos().get(0).id());
    assertEquals(fideos.id(), guardado.getSubcategoriaId());
    assertEquals(alimentos.id(), guardado.getCategoriaId());
  }

  @Test
  @DisplayName("Una categoria puede tener varias subcategorias")
  void categoriaConVariasSubcategorias() {
    CategoriaDTO alimentos = categoria("Alimentos");
    subcategoria("Fideos", alimentos.id());
    subcategoria("Arroz", alimentos.id());
    subcategoria("Legumbres", alimentos.id());

    assertEquals(3, fachada.listarSubcategoriasDe(alimentos.id()).size());
  }

  @Test
  @DisplayName("No se acepta una subcategoria que no pertenece a la categoria declarada")
  void subcategoriaDeOtraCategoria() {
    CategoriaDTO alimentos = categoria("Alimentos");
    CategoriaDTO vestimenta = categoria("Vestimenta");
    CategoriaDTO camperas = subcategoria("Camperas", vestimenta.id());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.agregarProducto(
                new ProductoDTO(null, "Arroz", "un kilo de arroz", alimentos.id(), null),
                camperas.id()));
  }

  @Test
  @DisplayName("Una categoria raiz no puede usarse como subcategoria")
  void categoriaRaizNoEsSubcategoria() {
    CategoriaDTO alimentos = categoria("Alimentos");

    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.agregarProducto(
                new ProductoDTO(null, "Arroz", "un kilo de arroz", null, null), alimentos.id()));
  }

  @Test
  @DisplayName("La jerarquia es de un solo nivel: una subcategoria no puede tener subcategorias")
  void jerarquiaDeUnSoloNivel() {
    CategoriaDTO alimentos = categoria("Alimentos");
    CategoriaDTO fideos = subcategoria("Fideos", alimentos.id());

    assertThrows(
        IllegalArgumentException.class, () -> subcategoria("Tallarines", fideos.id()));
  }

  // ------------------------------------------------------------ ABM: baja

  @Test
  @DisplayName("Se puede dar de baja un producto que no tiene donaciones")
  void bajaDeProducto() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));

    fachada.eliminarProducto(producto.id());

    assertTrue(fachada.listarProductos().isEmpty());
  }

  @Test
  @DisplayName("No se puede dar de baja un producto con donaciones asociadas")
  void bajaDeProductoEnUso() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));
    fachada.registrarDonacion(
        new DonacionDTO(null, "donador1", "dep1", "desc", producto.id(), 5, null));

    assertThrows(RecursoEnUsoException.class, () -> fachada.eliminarProducto(producto.id()));
  }

  @Test
  @DisplayName("No se puede dar de baja una categoria que tiene subcategorias")
  void bajaDeCategoriaConSubcategorias() {
    CategoriaDTO alimentos = categoria("Alimentos");
    subcategoria("Fideos", alimentos.id());

    assertThrows(RecursoEnUsoException.class, () -> fachada.eliminarCategoria(alimentos.id()));
  }

  @Test
  @DisplayName("No se puede dar de baja un identificador usado por un producto")
  void bajaDeIdentificadorEnUso() {
    String ident = identificador(TipoIdentificadorEnum.CODIGODEBARRAS).id();
    fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa de roble", null, ident));

    assertThrows(RecursoEnUsoException.class, () -> fachada.eliminarIdentificador(ident));
  }

  // --------------------------------------------------- ABM: modificacion

  @Test
  @DisplayName("Al modificar un producto se revalida el identificador contra el nombre nuevo")
  void modificarProductoRevalidaIdentificador() {
    String ident = identificador(TipoIdentificadorEnum.QR).id();
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, ident));

    // "Silla" tiene 5 letras: impar, deja de ser valido para QR.
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.modificarProducto(
                producto.id(), new ProductoDTO(null, "Silla", "una silla", null, ident), null));
  }

  @Test
  @DisplayName("Se puede modificar una categoria")
  void modificarCategoria() {
    CategoriaDTO alimentos = categoria("Alimentos");

    CategoriaDTO modificada =
        fachada.modificarCategoria(
            alimentos.id(), new CategoriaDTO(null, "Alimentos secos", "otra desc", null));

    assertEquals("Alimentos secos", modificada.nombre());
  }

  @Test
  @DisplayName("Se puede modificar un identificador")
  void modificarIdentificador() {
    String ident = identificador(TipoIdentificadorEnum.QR).id();

    IdentificadorDTO modificado =
        fachada.modificarIdentificador(
            ident, new IdentificadorDTO(null, TipoIdentificadorEnum.CODIGODEBARRAS, "otra"));

    assertEquals(TipoIdentificadorEnum.CODIGODEBARRAS, modificado.tipo());
  }

  // -------------------------------------------------- donaciones y estados

  @Test
  @DisplayName("Se rechaza una donacion con cantidad nula, cero o negativa")
  void cantidadInvalida() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.registrarDonacion(
                new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), null, null)));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.registrarDonacion(
                new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), 0, null)));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fachada.registrarDonacion(
                new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), -5, null)));
  }

  @Test
  @DisplayName("Un donador que no puede donar produce un rechazo, no un error generico")
  void donadorNoHabilitado() {
    when(donadores.puedeDonar("donador1")).thenReturn(Boolean.FALSE);
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));

    assertThrows(
        DonacionRechazadaException.class,
        () ->
            fachada.registrarDonacion(
                new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), 5, null)));
  }

  @Test
  @DisplayName("El historial de estados es consultable y queda en orden")
  void historialDeEstados() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));
    DonacionDTO donacion =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), 5, null));

    fachada.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.ACEPTADA);
    fachada.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.CONQUEJA);

    List<CambioEstadoDonacion> historial = fachada.obtenerHistorialDeEstados(donacion.id());

    assertEquals(3, historial.size());
    assertEquals(EstadoDonacion.INGRESADA, historial.get(0).getEstado());
    assertEquals(EstadoDonacion.ACEPTADA, historial.get(1).getEstado());
    assertEquals(EstadoDonacion.CONQUEJA, historial.get(2).getEstado());
  }

  @Test
  @DisplayName("El historial no se puede alterar desde afuera")
  void historialEsDeSoloLectura() {
    ProductoDTO producto =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));
    DonacionDTO donacion =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "d", producto.id(), 5, null));

    List<CambioEstadoDonacion> historial = fachada.obtenerHistorialDeEstados(donacion.id());

    assertThrows(UnsupportedOperationException.class, historial::clear);
  }

  // ------------------------------------------------------ ids numericos

  @Test
  @DisplayName("Los identificadores de entidad son numericos y los asigna este componente")
  void idsNumericosYSecuenciales() {
    ProductoDTO primero =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));
    ProductoDTO segundo =
        fachada.agregarProducto(new ProductoDTO(null, "Silla", "una silla", null, null));

    assertEquals("1", primero.id());
    assertEquals("2", segundo.id());
    assertEquals(1L, Long.parseLong(primero.id()));
  }
}

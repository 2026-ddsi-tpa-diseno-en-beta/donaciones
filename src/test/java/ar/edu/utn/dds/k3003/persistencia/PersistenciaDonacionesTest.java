package ar.edu.utn.dds.k3003.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.app.Application;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import ar.edu.utn.dds.k3003.model.CambioEstadoDonacion;
import ar.edu.utn.dds.k3003.model.EstadoDonacion;
import ar.edu.utn.dds.k3003.model.Producto;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ejercita el camino JPA, que es el unico que corre en produccion. Los demas tests usan los
 * repositorios en memoria, con lo cual el mapeo real (sobre todo el {@code @ElementCollection} del
 * historial de estados) nunca se verificaba.
 */
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
class PersistenciaDonacionesTest {

  @Autowired private Fachada fachada;
  @Autowired private EntityManager entityManager;

  private String productoId;

  @BeforeEach
  void setUp() {
    fachada.limpiarDatos();

    IdentificadorDTO identificador =
        fachada.agregarIdentificador(
            new IdentificadorDTO(null, TipoIdentificadorEnum.CODIGODEBARRAS, "codigo"));
    CategoriaDTO alimentos =
        fachada.agregarCategoria(new CategoriaDTO(null, "Alimentos", "comida", null));
    CategoriaDTO fideos =
        fachada.agregarCategoria(new CategoriaDTO(null, "Fideos", "pastas secas", null), alimentos.id());

    productoId =
        fachada
            .agregarProducto(
                new ProductoDTO(
                    null, "Fideos", "medio kilo de fideos", alimentos.id(), identificador.id()),
                fideos.id())
            .id();
  }

  /** Fuerza un ida y vuelta real a la base para no leer del cache de primer nivel. */
  private void irYVolverDeLaBase() {
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("El historial de estados se persiste completo y en orden")
  void historialSobreviveAlIdaYVueltaDeLaBase() {
    DonacionDTO donacion =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "una donacion", productoId, 10, null));

    fachada.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.ACEPTADA);
    fachada.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.CONQUEJA);

    irYVolverDeLaBase();

    List<CambioEstadoDonacion> historial = fachada.obtenerHistorialDeEstados(donacion.id());

    assertEquals(3, historial.size(), "los 3 cambios tienen que estar en la base");
    assertEquals(EstadoDonacion.INGRESADA, historial.get(0).getEstado());
    assertEquals(EstadoDonacion.ACEPTADA, historial.get(1).getEstado());
    assertEquals(EstadoDonacion.CONQUEJA, historial.get(2).getEstado());
    assertNotNull(historial.get(0).getFechaCambio(), "la fecha se persiste");
    assertEquals("una donacion", fachada.buscarDonacionPorID(donacion.id()).descripcion());
  }

  @Test
  @DisplayName("El estado actual se deriva del historial persistido")
  void estadoActualDespuesDeRecargar() {
    DonacionDTO donacion =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "d", productoId, 10, null));
    fachada.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.ACEPTADA);

    irYVolverDeLaBase();

    assertEquals(
        EstadoDonacionEnum.ACEPTADA, fachada.buscarDonacionPorID(donacion.id()).estado());
  }

  @Test
  @DisplayName("La subcategoria del producto se persiste")
  void subcategoriaSePersiste() {
    irYVolverDeLaBase();

    Producto producto = fachada.verProducto(productoId);
    assertNotNull(producto.getSubcategoriaId());
    assertNotNull(producto.getCategoriaId());
  }

  @Test
  @DisplayName("Un producto sin identificador se persiste con el identificador nulo")
  void productoSinIdentificadorSePersiste() {
    ProductoDTO sinIdentificador =
        fachada.agregarProducto(new ProductoDTO(null, "Mesa", "una mesa", null, null));

    irYVolverDeLaBase();

    assertNull(fachada.verProducto(sinIdentificador.id()).getIdentificadorId());
  }

  @Test
  @DisplayName("La busqueda por donador y fecha va contra la base")
  void busquedaPorDonadorYFecha() {
    fachada.registrarDonacion(
        new DonacionDTO(null, "donador1", "dep1", "d", productoId, 10, null));
    fachada.registrarDonacion(
        new DonacionDTO(null, "donador2", "dep1", "d", productoId, 10, null));

    irYVolverDeLaBase();

    List<DonacionDTO> delDonador1 =
        fachada.buscarPorDonadorYFechaInicio("donador1", LocalDate.now().minusDays(1));

    assertEquals(1, delDonador1.size());
    assertEquals("donador1", delDonador1.get(0).donadorID());
  }

  @Test
  @DisplayName("Los IDs numericos se generan desde una secuencia persistida")
  void idsNumericosPersistidos() {
    DonacionDTO primera =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "d", productoId, 1, null));
    DonacionDTO segunda =
        fachada.registrarDonacion(
            new DonacionDTO(null, "donador1", "dep1", "d", productoId, 1, null));

    irYVolverDeLaBase();

    long primeraId = Long.parseLong(primera.id());
    long segundaId = Long.parseLong(segunda.id());

    assertTrue(segundaId > primeraId, "la secuencia tiene que avanzar");
    assertNotNull(fachada.buscarDonacionPorID(primera.id()));
  }

  @Test
  @DisplayName("Limpiar la base deja el modulo vacio")
  void limpiarDatos() {
    fachada.registrarDonacion(
        new DonacionDTO(null, "donador1", "dep1", "d", productoId, 10, null));

    fachada.limpiarDatos();
    irYVolverDeLaBase();

    assertTrue(fachada.listarDonaciones().isEmpty());
    assertTrue(fachada.listarProductos().isEmpty());
    assertTrue(fachada.listarCategorias().isEmpty());
    assertTrue(fachada.listarIdentificadores().isEmpty());
  }
}

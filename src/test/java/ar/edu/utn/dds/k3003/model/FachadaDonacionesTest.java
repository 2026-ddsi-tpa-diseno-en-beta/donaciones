package ar.edu.utn.dds.k3003.model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FachadaDonacionesTest {

  Fachada fachada;

  @Mock FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
  @Mock FachadaLogistica fachadaLogistica;

  DonadorDTO donadorEjemplo;
  DonacionDTO donacionEjemplo;

  @BeforeEach
  void setUp() {
    fachada = new Fachada();
    fachada.setFachadaDonadoresYEntidades(fachadaDonadoresYEntidades);
    fachada.setFachadaLogistica(fachadaLogistica);

    donadorEjemplo =
        new DonadorDTO(
            "donador1",
            "Federico",
            "Bremberg",
            25,
            "fede@mail.com",
            "40000000",
            "Calle 1",
            null,
            "Ocasional");

    donacionEjemplo =
        new DonacionDTO(
            null,
            "donador1",
            "deposito1",
            "10 paquetes de arroz",
            "producto1",
            10,
            EstadoDonacionEnum.INGRESADA);
  }

  private void stubDonadorHabilitado() {
    when(fachadaDonadoresYEntidades.buscarDonadorPorID("donador1")).thenReturn(donadorEjemplo);
    when(fachadaDonadoresYEntidades.puedeDonar("donador1")).thenReturn(Boolean.TRUE);
    when(fachadaLogistica.gestionarDonacion(any(), any(), any(), anyInt()))
        .thenReturn(new DepositoDTO("deposito1", "Depósito Central", "Calle 123", 1000, null));
  }

  @Test
  void registrarDonacionAsignaIdYDerivaALogistica() {
    stubDonadorHabilitado();

    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    Assertions.assertNotNull(registrada.id());
    Assertions.assertEquals(EstadoDonacionEnum.INGRESADA, registrada.estado());
    Assertions.assertEquals("donador1", registrada.donadorID());
    Assertions.assertEquals(10, registrada.cantidad());

    verify(fachadaDonadoresYEntidades, times(1)).buscarDonadorPorID("donador1");
    verify(fachadaDonadoresYEntidades, times(1)).puedeDonar("donador1");
    verify(fachadaLogistica, times(1))
        .gestionarDonacion("deposito1", registrada.id(), "producto1", 10);
  }

  @Test
  void registrarDonacionSinFachadaLogistica() {
    when(fachadaDonadoresYEntidades.buscarDonadorPorID("donador1")).thenReturn(donadorEjemplo);
    when(fachadaDonadoresYEntidades.puedeDonar("donador1")).thenReturn(Boolean.TRUE);
    fachada.setFachadaLogistica(null);

    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    Assertions.assertNotNull(registrada.id());
    Assertions.assertEquals(EstadoDonacionEnum.INGRESADA, registrada.estado());
  }

  @Test
  void registrarDonacionRechazaDonadorBaneado() {
    when(fachadaDonadoresYEntidades.buscarDonadorPorID("donador1")).thenReturn(donadorEjemplo);
    when(fachadaDonadoresYEntidades.puedeDonar("donador1")).thenReturn(Boolean.FALSE);

    Assertions.assertThrows(
        RuntimeException.class, () -> fachada.registrarDonacion(donacionEjemplo));

    verify(fachadaLogistica, times(0)).gestionarDonacion(any(), any(), any(), anyInt());
  }

  @Test
  void buscarDonacionPorIDRecuperaLaDonacionRegistrada() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    DonacionDTO encontrada = fachada.buscarDonacionPorID(registrada.id());

    Assertions.assertEquals(registrada.id(), encontrada.id());
    Assertions.assertEquals(registrada.donadorID(), encontrada.donadorID());
    Assertions.assertEquals(registrada.cantidad(), encontrada.cantidad());
  }

  @Test
  void buscarDonacionPorIDInexistenteLanzaExcepcion() {
    Assertions.assertThrows(RuntimeException.class, () -> fachada.buscarDonacionPorID("no-existe"));
  }

  @Test
  void cambiarEstadoDeDonacionPersisteLaTransicion() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    DonacionDTO actualizada =
        fachada.cambiarEstadoDeDonacion(registrada.id(), EstadoDonacionEnum.ACEPTADA);

    Assertions.assertEquals(EstadoDonacionEnum.ACEPTADA, actualizada.estado());
    Assertions.assertEquals(
        EstadoDonacionEnum.ACEPTADA, fachada.buscarDonacionPorID(registrada.id()).estado());
  }

  @Test
  void cambiarEstadoDeDonacionConEstadoNuloFalla() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    Assertions.assertThrows(
        RuntimeException.class, () -> fachada.cambiarEstadoDeDonacion(registrada.id(), null));
  }

  @Test
  void registrarQuejaNotificaDonadoresYCambiaEstadoAConQueja() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);
    fachada.cambiarEstadoDeDonacion(registrada.id(), EstadoDonacionEnum.ACEPTADA);

    when(fachadaDonadoresYEntidades.agregarQueja(any()))
        .thenReturn(new QuejaDTO("q1", registrada.id(), "donador1", LocalDate.now(), "mal estado"));

    DonacionDTO conQueja = fachada.registrarQuejaEnDonacion(registrada.id(), "mal estado");

    Assertions.assertEquals(EstadoDonacionEnum.CONQUEJA, conQueja.estado());
    verify(fachadaDonadoresYEntidades, times(1)).agregarQueja(any());
  }

  @Test
  void registrarQuejaFallaSiNoSePuedeNotificarYPreservaEstado() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);
    fachada.cambiarEstadoDeDonacion(registrada.id(), EstadoDonacionEnum.ACEPTADA);

    when(fachadaDonadoresYEntidades.agregarQueja(any()))
        .thenThrow(new RuntimeException("donador no existe"));

    Assertions.assertThrows(
        RuntimeException.class,
        () -> fachada.registrarQuejaEnDonacion(registrada.id(), "mal estado"));
    Assertions.assertEquals(
        EstadoDonacionEnum.ACEPTADA, fachada.buscarDonacionPorID(registrada.id()).estado());
  }

  @Test
  void buscarPorDonadorYFechaInicioIncluyeDonacionesDesdeLaFecha() {
    stubDonadorHabilitado();
    DonacionDTO registrada = fachada.registrarDonacion(donacionEjemplo);

    List<DonacionDTO> resultado =
        fachada.buscarPorDonadorYFechaInicio("donador1", LocalDate.now().minusDays(1));

    Assertions.assertEquals(1, resultado.size());
    Assertions.assertEquals(registrada.id(), resultado.get(0).id());
  }

  @Test
  void buscarPorDonadorYFechaInicioExcluyeDonacionesAnteriores() {
    stubDonadorHabilitado();
    fachada.registrarDonacion(donacionEjemplo);

    List<DonacionDTO> resultado =
        fachada.buscarPorDonadorYFechaInicio("donador1", LocalDate.now().plusDays(1));

    Assertions.assertTrue(resultado.isEmpty());
  }

  @Test
  void buscarPorDonadorYFechaInicioLanzaSiDonadorNoExiste() {
    Assertions.assertThrows(
        RuntimeException.class,
        () -> fachada.buscarPorDonadorYFechaInicio("inexistente", LocalDate.now()));
  }

  @Test
  void buscarPorDonadorYFechaInicioRetornaVacioSiNoHayDonaciones() {
    when(fachadaDonadoresYEntidades.buscarDonadorPorID("donador2")).thenReturn(donadorEjemplo);

    List<DonacionDTO> resultado = fachada.buscarPorDonadorYFechaInicio("donador2", LocalDate.now());

    Assertions.assertTrue(resultado.isEmpty());
    verify(fachadaDonadoresYEntidades, times(1)).buscarDonadorPorID("donador2");
  }
}

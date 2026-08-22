package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.EstadoDonacion;
import ar.edu.utn.dds.k3003.repositories.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories.ProductosRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Registro anticipado de las metricas del modulo. Los contadores de Micrometer aparecen recien en
 * el primer incremento, y como Render reinicia el servicio al dormirlo, el modulo arrancaba "sin
 * metricas" hasta que ocurria cada evento. Registrarlas aca al levantar el contexto hace que esten
 * siempre visibles (en 0 si todavia no paso nada) y que un dashboard pueda armarse de antemano.
 *
 * <p>Ademas expone gauges con el estado actual de la base (donaciones por estado, productos,
 * categorias, identificadores): los contadores miden lo que PASO desde el ultimo reinicio, los
 * gauges muestran lo que HAY, que es lo util para mirar en vivo durante una demo.
 */
@Component
public class MetricasDonaciones {

  /** Mismos nombres que incrementa la Fachada: mantener sincronizados. */
  private static final List<String> CONTADORES =
      List.of(
          "donatrack.donaciones.registradas",
          "donatrack.donaciones.aceptadas",
          "donatrack.donaciones.rechazadas",
          "donatrack.donaciones.quejas.registradas",
          "donatrack.donaciones.productos.registrados",
          "donatrack.donaciones.productos.eliminados",
          "donatrack.donaciones.identificadores.registrados",
          "donatrack.donaciones.categorias.registradas");

  /** Mismos nombres de componente que usa medirIntegracion(). */
  private static final List<String> COMPONENTES_INTEGRADOS = List.of("donadores", "logistica");

  /**
   * El estado de una donacion se deriva del historial, no hay columna para contar con una query.
   * Para no recorrer la tabla en cada scrape, el conteo por estado se cachea unos segundos: para
   * un gauge de dashboard la frescura al segundo no aporta nada.
   */
  private static final Duration TTL_SNAPSHOT = Duration.ofSeconds(10);

  private final DonacionesRepository donacionesRepository;
  private volatile Map<EstadoDonacion, Long> snapshotPorEstado = new EnumMap<>(EstadoDonacion.class);
  private volatile Instant snapshotTomadoEn = Instant.MIN;

  public MetricasDonaciones(
      MeterRegistry registry,
      DonacionesRepository donacionesRepository,
      ProductosRepository productosRepository,
      CategoriasRepository categoriasRepository,
      IdentificadoresRepository identificadoresRepository) {
    this.donacionesRepository = donacionesRepository;

    CONTADORES.forEach(nombre -> Counter.builder(nombre).register(registry));
    COMPONENTES_INTEGRADOS.forEach(
        componente ->
            Counter.builder("donatrack.donaciones.integracion." + componente + ".errores")
                .register(registry));

    for (EstadoDonacion estado : EstadoDonacion.values()) {
      Gauge.builder("donatrack.donaciones.actuales", () -> contarPorEstado(estado))
          .tag("estado", estado.name())
          .description("Donaciones actualmente en este estado")
          .register(registry);
    }
    Gauge.builder(
            "donatrack.donaciones.productos.actuales",
            () -> productosRepository.findAll().size())
        .register(registry);
    Gauge.builder(
            "donatrack.donaciones.categorias.actuales",
            () -> categoriasRepository.findAll().size())
        .register(registry);
    Gauge.builder(
            "donatrack.donaciones.identificadores.actuales",
            () -> identificadoresRepository.findAll().size())
        .register(registry);
  }

  private long contarPorEstado(EstadoDonacion estado) {
    if (Duration.between(snapshotTomadoEn, Instant.now()).compareTo(TTL_SNAPSHOT) > 0) {
      refrescarSnapshot();
    }
    return snapshotPorEstado.getOrDefault(estado, 0L);
  }

  private synchronized void refrescarSnapshot() {
    if (Duration.between(snapshotTomadoEn, Instant.now()).compareTo(TTL_SNAPSHOT) <= 0) {
      return; // otro scrape lo refresco mientras esperabamos el lock
    }
    Map<EstadoDonacion, Long> conteo = new EnumMap<>(EstadoDonacion.class);
    for (Donacion donacion : donacionesRepository.findAll()) {
      conteo.merge(donacion.getEstadoActual(), 1L, Long::sum);
    }
    snapshotPorEstado = conteo;
    snapshotTomadoEn = Instant.now();
  }
}

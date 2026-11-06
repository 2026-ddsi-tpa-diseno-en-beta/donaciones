package ar.edu.utn.dds.k3003.integrations;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class HttpContractsTest {
  @Test
  void remoteAdaptersPreserveDomainReferencesAndTrace() throws Exception {
    org.slf4j.MDC.put("traceId", "contract-trace");
    io.micrometer.core.instrument.Metrics.globalRegistry.clear();
    var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    AtomicReference<String> body = new AtomicReference<>(), trace = new AtomicReference<>();
    server.createContext(
        "/",
        e -> {
          body.set(new String(e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
          trace.set(e.getRequestHeaders().getFirst("X-Trace-Id"));
          String path = e.getRequestURI().getPath();
          String content =
              path.endsWith("puede-donar")
                  ? "true"
                  : path.startsWith("/depositos/")
                      ? "{\"id\":\"z\",\"nombre\":\"Central\",\"direccion\":\"Medrano\",\"capacidadMaxima\":100,\"stockActual\":[]}"
                      : "{\"id\":\"d\",\"nombre\":\"Ana\",\"apellido\":\"Demo\",\"edad\":25,\"estado\":\"VERIFICADO\",\"categoria\":\"OCASIONAL\"}";
          byte[] output = content.getBytes(StandardCharsets.UTF_8);
          e.getResponseHeaders().add("Content-Type", "application/json");
          e.sendResponseHeaders(path.contains("missing") ? 404 : 200, output.length);
          e.getResponseBody().write(output);
          e.close();
        });
    server.start();
    try {
      String url = "http://127.0.0.1:" + server.getAddress().getPort();
      var donors = new FachadaDonadoresYEntidadesHttp(url);
      var logistics = new FachadaLogisticaHttp(url);
      assertEquals("d", donors.buscarDonadorPorID("d").id());
      assertNotNull(trace.get());
      assertTrue(donors.puedeDonar("d"));
      var complaint = new QuejaDTO(null, "x", "d", LocalDate.of(2026, 11, 6), "Defectuoso");
      assertEquals(complaint, donors.agregarQueja(complaint));
      assertTrue(body.get().contains("2026-11-06"));
      assertEquals("z", logistics.buscarDepositoPorID("z").id());
      assertEquals("z", logistics.gestionarDonacion("z", "x", "p", 3).id());
      assertTrue(body.get().contains("\"cantidad\":3"));
      assertThrows(
          org.springframework.web.client.HttpClientErrorException.NotFound.class,
          () -> donors.buscarDonadorPorID("missing"));
    } finally {
      server.stop(0);
      org.slf4j.MDC.clear();
      io.micrometer.core.instrument.Metrics.globalRegistry.clear();
    }
  }
}

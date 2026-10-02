package ar.edu.utn.dds.k3003.observability;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.http.client.*;
import org.springframework.http.*;
import java.net.URI;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class TracePropagationInterceptorTest {
  @Test void recordsRealStatusAndTimeoutWithoutChangingResponseOrContext() throws Exception {
    var registry = new SimpleMeterRegistry();
    Metrics.addRegistry(registry);
    MDC.put("traceId", "trace-test"); MDC.put("event", "outer");
    try {
      var interceptor = new TracePropagationInterceptor();
      for (int status : new int[]{200,400,503}) {
        var request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/recurso/private-id?q=private"));
        var response = new MockClientHttpResponse(new byte[]{1}, HttpStatusCode.valueOf(status));
        assertSame(response, interceptor.intercept(request, new byte[0], (r,b) -> response));
        assertEquals("trace-test", request.getHeaders().getFirst("X-Trace-Id"));
        assertEquals(1, registry.get("donatrack.integraciones.duracion").tag("status", String.valueOf(status)).timer().count());
      }
      var request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/recurso"));
      assertThrows(IOException.class, () -> interceptor.intercept(request, new byte[0], (r,b) -> {throw new IOException("timeout");}));
      assertEquals(1, registry.get("donatrack.integraciones.duracion").tag("status", "error").timer().count());
      assertEquals("outer", MDC.get("event")); assertNull(MDC.get("destination"));
      assertTrue(registry.getMeters().stream().allMatch(m -> m.getId().getTag("destino").equals("localhost")));
    } finally { MDC.clear(); Metrics.removeRegistry(registry); registry.close(); }
  }
}

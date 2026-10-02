package ar.edu.utn.dds.k3003.observability;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
  private final String component;
  private final String instance;
  public RequestLoggingFilter(
      @Value("${spring.application.name:donaciones}") String component,
      @Value("${INSTANCE_ID:${HOSTNAME:local}}") String instance) {
    this.component = component;
    this.instance = instance;
  }
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/actuator") || request.getRequestURI().equals("/ping");
  }
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    Map<String, String> previous = MDC.getCopyOfContextMap();
    String trace = request.getHeader("X-Trace-Id");
    if (trace == null || !trace.matches("[A-Za-z0-9_-]{1,128}")) trace = UUID.randomUUID().toString();
    MDC.put("traceId", trace);
    MDC.put("requestId", UUID.randomUUID().toString());
    MDC.put("component", component);
    MDC.put("instanceId", instance);
    response.setHeader("X-Trace-Id", trace);
    long start = System.nanoTime();
    boolean failed = false;
    try {
      chain.doFilter(request, response);
    } catch (IOException | ServletException | RuntimeException ex) {
      failed = true;
      throw ex;
    } finally {
      long millis = (System.nanoTime() - start) / 1_000_000;
      int status = failed ? 500 : response.getStatus();
      MDC.put("event", "http.finalizado"); MDC.put("status", String.valueOf(status));
      MDC.put("durationMs", String.valueOf(millis));
      MDC.put("outcome", status >= 500 ? "error" : status >= 400 ? "rechazada" : "ok");
      if (status >= 500) log.error("http.finalizado metodo={} ruta={} status={} duracion_ms={}", request.getMethod(), request.getRequestURI(), status, millis);
      else if (status >= 400) log.warn("http.finalizado metodo={} ruta={} status={} duracion_ms={}", request.getMethod(), request.getRequestURI(), status, millis);
      else if (!request.getMethod().equals("GET")) log.info("http.finalizado metodo={} ruta={} status={} duracion_ms={}", request.getMethod(), request.getRequestURI(), status, millis);
      MDC.clear();
      if (previous != null) MDC.setContextMap(previous);
    }
  }
}

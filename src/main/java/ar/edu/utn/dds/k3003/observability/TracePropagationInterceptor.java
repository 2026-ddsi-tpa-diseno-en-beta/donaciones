package ar.edu.utn.dds.k3003.observability;

import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;

public class TracePropagationInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    String trace = MDC.get("traceId");
    if (trace != null) request.getHeaders().set("X-Trace-Id", trace);
    return execution.execute(request, body);
  }
}

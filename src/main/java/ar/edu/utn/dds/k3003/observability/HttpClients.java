package ar.edu.utn.dds.k3003.observability;

import java.time.Duration;
import java.net.http.HttpClient;
import java.util.List;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public final class HttpClients {
  private HttpClients() {}
  public static RestTemplate restTemplate() {
    var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15)).build());
    factory.setReadTimeout(Duration.ofSeconds(180));
    var client = new RestTemplate(factory);
    client.setInterceptors(List.of(new TracePropagationInterceptor()));
    return client;
  }
}

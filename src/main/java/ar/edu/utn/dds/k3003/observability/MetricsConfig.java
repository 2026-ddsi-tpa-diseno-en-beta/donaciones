package ar.edu.utn.dds.k3003.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MetricsConfig {
  @Bean
  MeterRegistryCustomizer<MeterRegistry> componentTags(
      @Value("${spring.application.name:donaciones}") String component) {
    return registry -> {
      registry.config().commonTags("component", component);
      for (String metric : java.util.List.of("donatrack.donaciones.registradas",
          "donatrack.donaciones.aceptadas", "donatrack.donaciones.rechazadas",
          "donatrack.donaciones.quejas.registradas")) registry.counter(metric);
    };
  }
}

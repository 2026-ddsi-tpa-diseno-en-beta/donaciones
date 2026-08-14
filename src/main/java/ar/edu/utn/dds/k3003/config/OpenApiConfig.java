package ar.edu.utn.dds.k3003.config;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  /** El DTO de catedra sirve tambien como response, pero estos campos no son entradas del alta. */
  @Bean
  public OpenApiCustomizer camposGeneradosDeDonacion() {
    return openApi -> {
      Schema<?> donacion = openApi.getComponents().getSchemas().get("DonacionDTO");
      if (donacion == null || donacion.getProperties() == null) {
        return;
      }
      marcarSoloLectura(donacion, "id");
      marcarSoloLectura(donacion, "estado");
    };
  }

  private void marcarSoloLectura(Schema<?> schema, String propiedad) {
    Schema<?> campo = (Schema<?>) schema.getProperties().get(propiedad);
    if (campo != null) {
      campo.setReadOnly(true);
    }
  }
}

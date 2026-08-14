package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.integrations.FachadaDonadoresYEntidadesHttp;
import ar.edu.utn.dds.k3003.integrations.FachadaDonadoresYEntidadesLocal;
import ar.edu.utn.dds.k3003.integrations.FachadaLogisticaHttp;
import ar.edu.utn.dds.k3003.integrations.FachadaLogisticaLocal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FachadasExternasConfig {

  private static final Logger log = LoggerFactory.getLogger(FachadasExternasConfig.class);

  /** Perfiles en los que se permite arrancar sin integraciones reales. */
  private static final String[] PERFILES_SIN_INTEGRACION = {"dev", "test"};

  @Bean
  public EstadoIntegraciones estadoIntegraciones(
      Fachada fachada,
      Environment environment,
      @Value("${donatrack.donadores-y-entidades.url:}") String donadoresUrl,
      @Value("${donatrack.logistica.url:}") String logisticaUrl) {

    boolean permiteFalsas = perfilPermiteFachadasLocales(environment);
    Map<String, String> modos = new LinkedHashMap<>();

    // Obligatorias: son las dos interacciones que el enunciado le pide a este componente
    // (Donaciones -> Donadores y Entidades, y Donaciones -> Logistica).
    exigirUrl("DONADORES_Y_ENTIDADES_URL", donadoresUrl, permiteFalsas);
    exigirUrl("LOGISTICA_URL", logisticaUrl, permiteFalsas);

    fachada.setFachadaDonadoresYEntidades(
        tieneUrl(donadoresUrl)
            ? new FachadaDonadoresYEntidadesHttp(donadoresUrl)
            : new FachadaDonadoresYEntidadesLocal());
    modos.put("donadoresYEntidades", modo(donadoresUrl));

    fachada.setFachadaLogistica(
        tieneUrl(logisticaUrl) ? new FachadaLogisticaHttp(logisticaUrl) : new FachadaLogisticaLocal());
    modos.put("logistica", modo(logisticaUrl));

    log.info("Integraciones configuradas: {}", modos);
    return new EstadoIntegraciones(modos);
  }

  /**
   * Sin URL configurada se instalaria una fachada local que acepta cualquier donador. Eso convierte
   * un error de configuracion en "el modulo no valida nada", en silencio, asi que fuera de dev/test
   * preferimos no arrancar.
   */
  private void exigirUrl(String variable, String url, boolean permiteFalsas) {
    if (tieneUrl(url)) {
      return;
    }
    if (!permiteFalsas) {
      throw new IllegalStateException(
          "Falta la variable de entorno "
              + variable
              + ". Sin ella el modulo usaria una fachada de prueba que acepta cualquier donador. "
              + "Configurala, o activa el perfil 'dev' si estas probando local.");
    }
    log.warn(
        "{} no esta configurada: se usa una fachada LOCAL de prueba. NO usar asi en produccion.",
        variable);
  }

  private boolean perfilPermiteFachadasLocales(Environment environment) {
    return environment.acceptsProfiles(
        org.springframework.core.env.Profiles.of(PERFILES_SIN_INTEGRACION));
  }

  private String modo(String url) {
    return tieneUrl(url) ? "HTTP " + url : "LOCAL (fachada de prueba)";
  }

  private boolean tieneUrl(String url) {
    return url != null && !url.isBlank();
  }
}

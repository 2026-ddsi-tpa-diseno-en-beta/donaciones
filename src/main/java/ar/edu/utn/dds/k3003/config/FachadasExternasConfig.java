package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.integrations.FachadaDonadoresYEntidadesHttp;
import ar.edu.utn.dds.k3003.integrations.FachadaDonadoresYEntidadesLocal;
import ar.edu.utn.dds.k3003.integrations.FachadaLogisticaHttp;
import ar.edu.utn.dds.k3003.integrations.FachadaLogisticaLocal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FachadasExternasConfig {

  public FachadasExternasConfig(
      Fachada fachada,
      @Value("${donatrack.donadores-y-entidades.url:}") String donadoresYEntidadesUrl,
      @Value("${donatrack.logistica.url:}") String logisticaUrl) {
    fachada.setFachadaDonadoresYEntidades(
        tieneUrl(donadoresYEntidadesUrl)
            ? new FachadaDonadoresYEntidadesHttp(donadoresYEntidadesUrl)
            : new FachadaDonadoresYEntidadesLocal());
    fachada.setFachadaLogistica(
        tieneUrl(logisticaUrl)
            ? new FachadaLogisticaHttp(logisticaUrl)
            : new FachadaLogisticaLocal());
  }

  private Boolean tieneUrl(String url) {
    return url != null && !url.isBlank();
  }
}

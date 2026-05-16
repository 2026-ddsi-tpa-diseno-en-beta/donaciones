package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.integrations.FachadaDonadoresYEntidadesLocal;
import ar.edu.utn.dds.k3003.integrations.FachadaLogisticaLocal;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FachadasExternasConfig {

  public FachadasExternasConfig(Fachada fachada) {
    fachada.setFachadaDonadoresYEntidades(new FachadaDonadoresYEntidadesLocal());
    fachada.setFachadaLogistica(new FachadaLogisticaLocal());
  }
}

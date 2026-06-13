package ar.edu.utn.dds.k3003.integrations;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.web.client.RestClient;

public class FachadaDonadoresYEntidadesHttp implements FachadaDonadoresYEntidades {
  private final RestClient restClient;

  public FachadaDonadoresYEntidadesHttp(String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  @Override
  public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
    return restClient.get().uri("/donadores/{id}", donadorID).retrieve().body(DonadorDTO.class);
  }

  @Override
  public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) {
    throw new UnsupportedOperationException();
  }

  @Override
  public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) {
    throw new UnsupportedOperationException();
  }

  @Override
  public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {
    QuejaRequest request =
        new QuejaRequest(
            quejaDTO.donacionID(),
            quejaDTO.donadorID(),
            quejaDTO.fecha() != null ? quejaDTO.fecha().toString() : null,
            quejaDTO.descripcion());

    restClient
        .post()
        .uri("/donadores/{id}/quejas", quejaDTO.donadorID())
        .body(request)
        .retrieve()
        .toBodilessEntity();

    return quejaDTO;
  }

  @Override
  public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
    return restClient
        .get()
        .uri("/donadores/{id}/puede-donar", donadorID)
        .retrieve()
        .body(Boolean.class);
  }

  @Override
  public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado)
      throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DonadorDTO modifcarCategoria(String donadorID, String categoria)
      throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
    throw new UnsupportedOperationException();
  }

  @Override
  public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad)
      throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DonadorStatsDTO estadisticasDonador(String donadorID) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {}

  private record QuejaRequest(
      String donacionID, String donadorID, String fecha, String descripcion) {}
}

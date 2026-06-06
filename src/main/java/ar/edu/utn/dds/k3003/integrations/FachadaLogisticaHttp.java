package ar.edu.utn.dds.k3003.integrations;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.web.client.RestClient;

public class FachadaLogisticaHttp implements FachadaLogistica {
  private final RestClient restClient;

  public FachadaLogisticaHttp(String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  @Override
  public DepositoDTO agregarDeposito(DepositoDTO deposito) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DepositoDTO buscarDepositoPorID(String depositoID) throws NoSuchElementException {
    return restClient.get().uri("/depositos/{id}", depositoID).retrieve().body(DepositoDTO.class);
  }

  @Override
  public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID) throws NoSuchElementException {
    throw new UnsupportedOperationException();
  }

  @Override
  public DepositoDTO gestionarDonacion(
      String depositoID, String donacionID, String productoID, Integer cantidad)
      throws NoSuchElementException {
    GestionDonacionRequest request = new GestionDonacionRequest(donacionID, productoID, cantidad);

    return restClient
        .post()
        .uri("/depositos/{id}/donacion", depositoID)
        .body(request)
        .retrieve()
        .body(DepositoDTO.class);
  }

  @Override
  public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AsignacionDTO ejecutarMatchmaking(
      String depositoID, PaqueteDTO paqueteDTO, List<NecesidadMaterialDTO> necesidades) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reportarEntrega(PaqueteDTO paqueteDTO) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {}

  @Override
  public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {}

  private record GestionDonacionRequest(String donacionID, String productoID, Integer cantidad) {}
}

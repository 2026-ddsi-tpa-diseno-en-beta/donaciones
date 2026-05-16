package ar.edu.utn.dds.k3003.integrations;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class FachadaDonadoresYEntidadesLocal implements FachadaDonadoresYEntidades {
  private final List<QuejaDTO> quejas = new ArrayList<>();
  private Integer idQueja = 1;

  @Override
  public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
    return donadorDTO;
  }

  @Override
  public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
    return new DonadorDTO(
        donadorID, "Donador", "Local", 30, "donador@mail.com", "0", "Sin domicilio", null, null);
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
    QuejaDTO quejaGuardada =
        new QuejaDTO(
            String.valueOf(idQueja++),
            quejaDTO.donacionID(),
            quejaDTO.donadorID(),
            LocalDate.now(),
            quejaDTO.descripcion());
    quejas.add(quejaGuardada);
    return quejaGuardada;
  }

  @Override
  public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
    return Boolean.TRUE;
  }

  @Override
  public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
    return quejas.stream().filter(q -> q.donadorID().equals(donadorID)).toList();
  }

  @Override
  public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado)
      throws NoSuchElementException {
    return buscarDonadorPorID(donadorID);
  }

  @Override
  public DonadorDTO modifcarCategoria(String donadorID, String categoria)
      throws NoSuchElementException {
    return buscarDonadorPorID(donadorID);
  }

  @Override
  public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
    return List.of();
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
}

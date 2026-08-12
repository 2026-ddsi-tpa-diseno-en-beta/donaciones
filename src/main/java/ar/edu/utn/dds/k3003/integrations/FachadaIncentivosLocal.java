package ar.edu.utn.dds.k3003.integrations;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import java.util.List;
import java.util.NoSuchElementException;

public class FachadaIncentivosLocal implements FachadaIncentivos {

  @Override
  public void procesarDonador(String donadorID) throws NoSuchElementException {}

  @Override
  public InsigniaDTO agregarInsignia(InsigniaDTO insignia) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MisionDTO agregarMision(MisionDTO mision) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) throws NoSuchElementException {
    return List.of();
  }

  @Override
  public MisionDTO getMisionEnCursoDeDonador(String donadorID) throws NoSuchElementException {
    return null;
  }

  @Override
  public void asignarMisionADonador(String donadorID, MisionDTO misionDTO)
      throws NoSuchElementException {}

  @Override
  public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO)
      throws NoSuchElementException {}

  @Override
  public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {}

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {}
}

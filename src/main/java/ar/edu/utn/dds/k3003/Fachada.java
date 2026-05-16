package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.repositories.DonacionesMapper;
import ar.edu.utn.dds.k3003.repositories.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.InMemoryDonacionesRepo;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Fachada implements FachadaDonaciones {

  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
  private FachadaLogistica fachadaLogistica;

  private final DonacionesRepository donacionesRepository;
  private final DonacionesMapper mapper;

  public Fachada() {
    this.donacionesRepository = new InMemoryDonacionesRepo();
    this.mapper = new DonacionesMapper();
  }

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
    this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
  }

  @Override
  public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
    this.fachadaLogistica = fachadaLogistica;
  }

  @Override
  public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
    if (donacionDTO == null) {
      throw new RuntimeException("La donación no puede ser nula");
    }
    if (donacionDTO.id() != null && donacionesRepository.findById(donacionDTO.id()).isPresent()) {
      throw new RuntimeException("La donación ya existe en el sistema");
    }

    DonadorDTO donador;
    try {
      donador = fachadaDonadoresYEntidades.buscarDonadorPorID(donacionDTO.donadorID());
    } catch (Exception e) {
      throw new RuntimeException("El donador indicado no existe", e);
    }
    if (donador == null) {
      throw new RuntimeException("El donador indicado no existe");
    }
    if (!Boolean.TRUE.equals(fachadaDonadoresYEntidades.puedeDonar(donacionDTO.donadorID()))) {
      throw new RuntimeException("El donador se encuentra baneado o no puede realizar donaciones");
    }

    Donacion donacion =
        new Donacion(
            donacionDTO.donadorID(),
            donacionDTO.depositoID(),
            donacionDTO.descripcion(),
            donacionDTO.productoID(),
            donacionDTO.cantidad());

    Donacion guardada = donacionesRepository.save(donacion);

    if (fachadaLogistica != null) {
      fachadaLogistica.gestionarDonacion(
          guardada.getDepositoId(),
          guardada.getId(),
          guardada.getProductoId(),
          guardada.getCantidad());
    }

    return mapper.toDTO(guardada);
  }

  @Override
  public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(
                () ->
                    new NoSuchElementException("No existe una donación con el ID: " + donacionID));
    return mapper.toDTO(donacion);
  }

  @Override
  public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado)
      throws NoSuchElementException {
    if (estado == null) {
      throw new RuntimeException("El nuevo estado no puede ser nulo");
    }

    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(() -> new NoSuchElementException("No existe la donación a modificar"));

    donacion.agregarCambioDeEstado(estado, "Cambio de estado reportado por Logística/Sistema");
    donacionesRepository.save(donacion);

    return mapper.toDTO(donacion);
  }

  @Override
  public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha)
      throws NoSuchElementException {
    List<Donacion> donacionesDelDonador = donacionesRepository.buscarPorDonador(donadorID);

    if (donacionesDelDonador.isEmpty()) {
      DonadorDTO donador;
      try {
        donador = fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
      } catch (Exception e) {
        throw new RuntimeException("El donador indicado no existe", e);
      }
      if (donador == null) {
        throw new RuntimeException("El donador indicado no existe");
      }
      return Collections.emptyList();
    }

    return donacionesDelDonador.stream()
        .filter(d -> !d.getHistorialEstados().get(0).getFechaCambio().toLocalDate().isBefore(fecha))
        .map(mapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
    if (donacionID == null) {
      throw new RuntimeException("El ID de la donación no puede ser nulo");
    }

    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(
                () -> new RuntimeException("No se encontró la donación para registrar la queja"));

    try {
      QuejaDTO quejaDTO =
          new QuejaDTO(
              null, donacion.getId(), donacion.getDonadorId(), LocalDate.now(), descripcion);
      fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
    } catch (Exception e) {
      throw new RuntimeException("No se pudo registrar la queja en el sistema de Donadores", e);
    }

    donacion.agregarCambioDeEstado(EstadoDonacionEnum.CONQUEJA, descripcion);
    donacionesRepository.save(donacion);

    return mapper.toDTO(donacion);
  }
}

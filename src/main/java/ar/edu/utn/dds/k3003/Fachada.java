package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.Identificador;
import ar.edu.utn.dds.k3003.model.Producto;
import ar.edu.utn.dds.k3003.repositories.DonacionesMapper;
import ar.edu.utn.dds.k3003.repositories.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories.IdentificadorMapper;
import ar.edu.utn.dds.k3003.repositories.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories.InMemoryDonacionesRepo;
import ar.edu.utn.dds.k3003.repositories.InMemoryIdentificadoresRepo;
import ar.edu.utn.dds.k3003.repositories.InMemoryProductosRepo;
import ar.edu.utn.dds.k3003.repositories.ProductoMapper;
import ar.edu.utn.dds.k3003.repositories.ProductosRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class Fachada implements FachadaDonaciones {

  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
  private FachadaLogistica fachadaLogistica;

  private final DonacionesRepository donacionesRepository;
  private final ProductosRepository productosRepository;
  private final IdentificadoresRepository identificadoresRepository;
  private final DonacionesMapper donacionesMapper;
  private final ProductoMapper productoMapper;
  private final IdentificadorMapper identificadorMapper;

  public Fachada() {
    this.donacionesRepository = new InMemoryDonacionesRepo();
    this.productosRepository = new InMemoryProductosRepo();
    this.identificadoresRepository = new InMemoryIdentificadoresRepo();
    this.donacionesMapper = new DonacionesMapper();
    this.productoMapper = new ProductoMapper();
    this.identificadorMapper = new IdentificadorMapper();
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
      throw new RuntimeException("La donacion no puede ser nula");
    }
    if (donacionDTO.id() != null && donacionesRepository.findById(donacionDTO.id()).isPresent()) {
      throw new RuntimeException("La donacion ya existe en el sistema");
    }
    if (productosRepository.findById(donacionDTO.productoID()).isEmpty()) {
      throw new RuntimeException("El producto indicado no existe");
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

    return donacionesMapper.toDTO(guardada);
  }

  @Override
  public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(
                () -> new NoSuchElementException("No existe una donacion con ese ID"));

    return donacionesMapper.toDTO(donacion);
  }

  @Override
  public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado)
      throws NoSuchElementException {
    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(() -> new NoSuchElementException("No existe la donacion a modificar"));

    donacion.cambiarEstado(estado, "Cambio de estado reportado por Logistica/Sistema");
    donacionesRepository.save(donacion);

    return donacionesMapper.toDTO(donacion);
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
        .map(donacionesMapper::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
    if (donacionID == null) {
      throw new RuntimeException("El ID de la donacion no puede ser nulo");
    }

    Donacion donacion =
        donacionesRepository
            .findById(donacionID)
            .orElseThrow(
                () -> new RuntimeException("No se encontro la donacion para registrar la queja"));

    if (!EstadoDonacionEnum.ACEPTADA.equals(donacion.getEstadoActual())) {
      throw new RuntimeException("Solo se puede registrar una queja sobre una donacion aceptada");
    }

    try {
      QuejaDTO quejaDTO =
          new QuejaDTO(
              null, donacion.getId(), donacion.getDonadorId(), LocalDate.now(), descripcion);
      fachadaDonadoresYEntidades.agregarQueja(quejaDTO);
    } catch (Exception e) {
      throw new RuntimeException("No se pudo registrar la queja en el sistema de Donadores", e);
    }

    donacion.cambiarEstado(EstadoDonacionEnum.CONQUEJA, descripcion);
    donacionesRepository.save(donacion);

    return donacionesMapper.toDTO(donacion);
  }

  @Override
  public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
    if (productoDTO == null) {
      throw new RuntimeException("El producto no puede ser nulo");
    }
    if (productoDTO.id() != null && productosRepository.findById(productoDTO.id()).isPresent()) {
      throw new RuntimeException("El producto ya existe en el sistema");
    }

    Identificador identificador =
        identificadoresRepository
            .findById(productoDTO.identificadorID())
            .orElseThrow(() -> new NoSuchElementException("No existe el identificador indicado"));

    Producto producto = productoMapper.toModel(productoDTO, identificador);
    Producto guardado = productosRepository.save(producto);

    return productoMapper.toDTO(guardado);
  }

  @Override
  public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
    Producto producto =
        productosRepository
            .findById(productoID)
            .orElseThrow(() -> new NoSuchElementException("No existe un producto con ese ID"));

    return productoMapper.toDTO(producto);
  }

  @Override
  public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
    if (identificadorDTO == null) {
      throw new RuntimeException("El identificador no puede ser nulo");
    }
    if (identificadorDTO.id() != null
        && identificadoresRepository.findById(identificadorDTO.id()).isPresent()) {
      throw new RuntimeException("El identificador ya existe en el sistema");
    }

    Identificador identificador = identificadorMapper.toModel(identificadorDTO);
    Identificador guardado = identificadoresRepository.save(identificador);

    return identificadorMapper.toDTO(guardado);
  }

  @Override
  public IdentificadorDTO buscarIdentificadorPorID(String identificadorID)
      throws NoSuchElementException {
    Identificador identificador =
        identificadoresRepository
            .findById(identificadorID)
            .orElseThrow(() -> new NoSuchElementException("No existe un identificador con ese ID"));

    return identificadorMapper.toDTO(identificador);
  }
}

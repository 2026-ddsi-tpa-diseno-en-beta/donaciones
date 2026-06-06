package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Donacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDonacionesRepository extends JpaRepository<Donacion, String> {
  List<Donacion> findByDonadorId(String donadorId);
}

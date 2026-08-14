package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Secuencia;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SpringDataSecuenciaRepository extends JpaRepository<Secuencia, String> {

  /** Bloqueo pesimista para que dos requests concurrentes no obtengan el mismo numero. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Secuencia> findByNombre(String nombre);
}

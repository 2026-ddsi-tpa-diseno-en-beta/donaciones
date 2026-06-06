package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Identificador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataIdentificadoresRepository extends JpaRepository<Identificador, String> {}

package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCategoriasRepository extends JpaRepository<Categoria, String> {}

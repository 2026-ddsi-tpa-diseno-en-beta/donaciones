package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductosRepository extends JpaRepository<Producto, String> {}

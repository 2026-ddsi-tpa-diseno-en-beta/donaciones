package ar.edu.utn.dds.k3003.repositories.springdata;

import ar.edu.utn.dds.k3003.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataProductosRepository extends JpaRepository<Producto, String> {

  @Query("select p from Producto p where p.categoriaId = :id or p.subcategoriaId = :id")
  List<Producto> findByCategoriaOSubcategoria(@Param("id") String id);

  List<Producto> findByIdentificadorId(String identificadorId);
}

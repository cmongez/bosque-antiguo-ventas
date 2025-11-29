package cl.bosqueantiguo.ventas.repository;

import cl.bosqueantiguo.ventas.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    // Método para cumplir con: GET /api/v1/sales/user/{userId}
    List<Boleta> findByUsuarioId(Long usuarioId);
}

package com.sonidovivo.backend.repository;
import com.sonidovivo.backend.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ProductoRepository extends JpaRepository<Producto,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.idProducto = :id")
    Optional<Producto> bloquear(@Param("id") Long id);
}

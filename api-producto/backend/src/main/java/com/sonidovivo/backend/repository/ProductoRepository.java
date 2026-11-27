package com.sonidovivo.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sonidovivo.backend.model.Producto;
import java.util.List;
import java.util.Optional;


public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivo(Integer activo);
    List<Producto> findByCategoriaIdCategoriaAndActivo(Long idCategoria, Integer activo);
    Optional<Producto> findByIdProductoAndActivo(Long idProducto, Integer activo);
}
package main.java.com.sonidovivo.backend.repository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivo(Integer activo);
    List<Producto> findByCategoriaIdCategoriaAndActivo(Long idCategoria, Integer activo);
    Optional<Producto> findByIdProductoAndActivo(Long idProducto, Integer activo);
}
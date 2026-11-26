package main.java.com.sonidovivo.backend.service;

import com.sonidovivo.producto.dto.ProductoEntradaDTO;
import com.sonidovivo.producto.dto.ProductoSalidaDTO;
import com.sonidovivo.producto.exception.RecursoNoEncontradoException;
import com.sonidovivo.producto.model.Categoria;
import com.sonidovivo.producto.model.Producto;
import com.sonidovivo.producto.repository.CategoriaRepository;
import com.sonidovivo.producto.repository.ProductoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public List<ProductoSalidaDTO> listarActivos() {
        return productos.findByActivo(1).stream()
                .map(this::salida)
                .toList();
    }

    public List<ProductoSalidaDTO> listarPorCategoria(Long idCategoria) {
        return productos.findByCategoriaIdCategoriaAndActivo(idCategoria, 1).stream()
                .map(this::salida)
                .toList();
    }

    public ProductoSalidaDTO buscarPorId(Long id) {
        return salida(obtenerActivo(id));
    }

    @Transactional
    public ProductoSalidaDTO crear(ProductoEntradaDTO d, Jwt actor) {
        comprobarAdministrador(actor);
        var categoria = obtenerCategoria(d.idCategoria());

        var p = new Producto();
        p.setCategoria(categoria);
        p.setNombre(d.nombre());
        p.setDescripcion(d.descripcion());
        p.setPrecio(d.precio());
        p.setStock(d.stock());
        p.setImagenUrl(d.imagenUrl());
        p.setActivo(1);

        return salida(productos.saveAndFlush(p));
    }

    @Transactional
    public ProductoSalidaDTO actualizar(Long id, ProductoEntradaDTO d, Jwt actor) {
        comprobarAdministrador(actor);
        var p = obtenerActivo(id);
        var categoria = obtenerCategoria(d.idCategoria());

        p.setCategoria(categoria);
        p.setNombre(d.nombre());
        p.setDescripcion(d.descripcion());
        p.setPrecio(d.precio());
        p.setStock(d.stock());
        p.setImagenUrl(d.imagenUrl());

        return salida(p);
    }

    @Transactional
    public void eliminar(Long id, Jwt actor) {
        comprobarAdministrador(actor);
        var p = obtenerActivo(id);
        p.setActivo(0);
    }

    private void comprobarAdministrador(Jwt actor) {
        if (actor == null || !"ADMINISTRADOR".equals(actor.getClaimAsString("rol"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Acceso denegado: se requiere rol de administrador.");
        }
    }

    private Producto obtenerActivo(Long id) {
        return productos.findByIdProductoAndActivo(id, 1)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));
    }

    private Categoria obtenerCategoria(Long idCategoria) {
        return categorias.findById(idCategoria)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La categoría especificada no existe."));
    }

    private ProductoSalidaDTO salida(Producto p) {
        return new ProductoSalidaDTO(
                p.getIdProducto(),
                p.getCategoria().getIdCategoria(),
                p.getCategoria().getNombre(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.getImagenUrl(),
                p.getActivo() == 1
        );
    }
}
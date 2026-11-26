package main.java.com.sonidovivo.backend.dto;
import java.math.BigDecimal;

public record ProductoSalidaDTO(
    Long id,
    Long idCategoria,
    String nombreCategoria,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    String imagenUrl,
    boolean activo
){}

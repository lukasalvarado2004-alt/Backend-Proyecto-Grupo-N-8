package main.java.com.sonidovivo.backend.dto;

public record ProductoEntradaDTO(
    @NotNull(message = "La categoría es obligatoria")
    Long idCategoria,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 120)
    String nombre,

    @Size(max = 500)
    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    BigDecimal precio,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock,

    @Size(max = 500)
    String imagenUrl
) {
    public ProductoEntradaDTO {
        nombre = nombre == null ? null : nombre.strip();
        descripcion = descripcion == null ? null : descripcion.strip();
        imagenUrl = imagenUrl == null ? null : imagenUrl.strip();
    }
}
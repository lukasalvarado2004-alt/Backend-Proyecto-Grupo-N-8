package com.sonidovivo.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoEntradaDTO(

    @NotNull(message = "La categoría es obligatoria")
    Long idCategoria,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150)
    String nombre,

    @Size(max = 500)
    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01")
    @Digits(integer = 8, fraction = 2)
    BigDecimal precio,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0)
    Integer stock,

    @Size(max = 255)
    String imagenUrl

) {
    public ProductoEntradaDTO {
        nombre = nombre == null ? null : nombre.strip();
        descripcion = descripcion == null ? null : descripcion.strip();
        imagenUrl = imagenUrl == null ? null : imagenUrl.strip();
    }
}
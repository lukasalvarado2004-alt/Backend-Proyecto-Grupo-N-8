package com.sonidovivo.backend.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record PedidoEntradaDTO(
    @NotBlank @Size(max=500) String direccionEnvio,
    @NotEmpty @Size(max=100) List<@NotNull @Valid Item> detalles
) {
    public record Item(@NotNull @Positive Long idProducto,
                       @NotNull @Min(1) @Max(1000000) Integer cantidad) {}
}

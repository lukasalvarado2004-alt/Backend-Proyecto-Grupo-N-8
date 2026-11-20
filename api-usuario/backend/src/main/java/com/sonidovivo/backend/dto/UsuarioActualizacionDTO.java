package com.sonidovivo.backend.dto;

import jakarta.validation.constraints.*;

public record UsuarioActualizacionDTO(
    @NotBlank @Size(min = 2, max = 80) String nombre,
    @NotBlank @Pattern(regexp = "\\+?[0-9]{8,15}") String telefono,
    @NotBlank @Size(min = 2, max = 80) String direccion
) {
    public UsuarioActualizacionDTO {
        nombre = nombre == null ? null : nombre.strip();
        direccion = direccion == null ? null : direccion.strip();
    }
}

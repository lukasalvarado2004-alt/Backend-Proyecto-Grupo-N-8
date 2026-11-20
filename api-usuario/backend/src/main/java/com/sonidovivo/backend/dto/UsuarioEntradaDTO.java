package com.sonidovivo.backend.dto;

import jakarta.validation.constraints.*;

public record UsuarioEntradaDTO(
    @NotBlank @Size(min = 2, max = 80) String nombre,
    @NotBlank @Email @Size(max = 254) String correo,
    @NotBlank @Size(min = 8, max = 72) String contrasena,
    @NotBlank @Pattern(regexp = "\\+?[0-9]{8,15}") String telefono,
    @NotBlank @Size(min = 2, max = 80) String direccion
) {
    public UsuarioEntradaDTO {
        nombre = nombre == null ? null : nombre.strip();
        direccion = direccion == null ? null : direccion.strip();
        correo = correo == null ? null : correo.strip();
    }

    @Override
    public String toString() {
        return "UsuarioEntradaDTO[datos protegidos]";
    }
}

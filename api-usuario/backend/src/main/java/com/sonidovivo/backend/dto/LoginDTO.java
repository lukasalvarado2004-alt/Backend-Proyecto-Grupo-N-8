package com.sonidovivo.backend.dto;

import jakarta.validation.constraints.*;

public record LoginDTO(
    @NotBlank @Email @Size(max = 254) String correo,
    @NotBlank @Size(max = 72) String contrasena
) {

    @Override
    public String toString() {
        return "LoginDTO[datos protegidos]";
    }
}

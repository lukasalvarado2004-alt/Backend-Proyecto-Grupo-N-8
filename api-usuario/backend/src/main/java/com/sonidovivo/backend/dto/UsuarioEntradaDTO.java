package com.sonidovivo.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;


@Data
@NoArgsConstructor
public class UsuarioEntradaDTO {

    @NotNull(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres.")
    private String nombre;

    @NotNull(message = "El correo es obligatorio.")
    @Email(message = "El email no tiene un formato válido")
    private String correo;

    @NotNull(message = "La contraseña es obligatoria.")
    @Size(min = 4, max = 80, message = "La contraseña debe tener entre 4 y 80 caracteres.")
    private String contrasena;

    
    @NotBlank(message = "El telefono es obligatorio.")
    @Pattern(regexp = "\\+?[0-9]{8,15}", message = "El telefono debe contener entre 8 y 15 digitos.")
    private String telefono;


    @NotNull(message = "La dirección es obligatorio.")
    @Size(min = 2, max = 80, message = "la direccion debe tener entre 2 y 80 caracteres.")
    private String direccion;

    @NotNull(message = "El rol es obligatorio.")
    @Size(min = 2, max = 80, message = "El rol debe tener entre 2 y 80 caracteres.")
    private String rol;

    // Los espacios a los lados no cuentan: se quitan antes de validar el largo.
    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.strip();
    }
}
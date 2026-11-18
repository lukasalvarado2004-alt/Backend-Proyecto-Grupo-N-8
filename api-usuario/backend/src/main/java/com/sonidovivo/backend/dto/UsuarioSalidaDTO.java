package com.sonidovivo.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSalidaDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String rol;
    private LocalDateTime fechaRegistro;
    
}

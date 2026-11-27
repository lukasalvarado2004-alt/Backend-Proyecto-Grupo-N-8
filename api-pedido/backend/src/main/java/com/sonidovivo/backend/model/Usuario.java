package com.sonidovivo.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity
@Table(name="USUARIOS")
@Getter
public class Usuario {
    @Id @Column(name="ID_USUARIO") private Long idUsuario;
    @Column(name="ROL",length=20,updatable=false) private String rol;
}

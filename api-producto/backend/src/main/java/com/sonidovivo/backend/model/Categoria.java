package com.sonidovivo.backend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CATEGORIA")
    private Long idCategoria;

    @Column(name = "NOMBRE", nullable = false,
            length = 80, unique = true)
    private String nombre;
}
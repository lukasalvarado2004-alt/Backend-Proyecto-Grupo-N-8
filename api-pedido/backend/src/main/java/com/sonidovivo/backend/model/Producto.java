package com.sonidovivo.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import org.hibernate.annotations.DynamicUpdate;
@Entity
@Table(name="PRODUCTOS")
@Getter @Setter @DynamicUpdate
public class Producto {
    @Id @Column(name="ID_PRODUCTO") private Long idProducto;
    @Column(name="PRECIO",precision=10,scale=2,nullable=false,updatable=false) private BigDecimal precio;
    @Column(name="STOCK",nullable=false) private Integer stock;
    @Column(name="ACTIVO",precision=1,scale=0,updatable=false) private Integer activo;
}

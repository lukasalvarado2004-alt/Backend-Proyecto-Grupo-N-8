package com.sonidovivo.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity
@Table(name="DETALLE_PEDIDOS")
@Getter @Setter
public class DetallePedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_DETALLE") private Long idDetalle;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="ID_PEDIDO",nullable=false) private Pedido pedido;
    @ManyToOne(fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="ID_PRODUCTO",nullable=false) private Producto producto;
    @Column(name="CANTIDAD",nullable=false) private Integer cantidad;
    @Column(name="PRECIO_UNITARIO",precision=10,scale=2,nullable=false) private BigDecimal precioUnitario;
}

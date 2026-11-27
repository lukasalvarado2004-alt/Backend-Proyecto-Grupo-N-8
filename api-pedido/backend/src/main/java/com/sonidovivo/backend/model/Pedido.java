package com.sonidovivo.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="PEDIDOS")
@Getter @Setter
public class Pedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID_PEDIDO") private Long idPedido;
    @Column(name="ID_USUARIO",nullable=false) private Long idUsuario;
    @Column(name="FECHA_PEDIDO",nullable=false) private LocalDateTime fechaPedido;
    @Column(name="TOTAL",precision=10,scale=2,nullable=false) private BigDecimal total;
    @Column(name="ESTADO_SEGUIMIENTO",length=30,nullable=false) private String estadoSeguimiento;
    @Column(name="DIRECCION_ENVIO",length=500,nullable=false) private String direccionEnvio;
    @Column(name="CODIGO_SEGUIMIENTO",length=50) private String codigoSeguimiento;
    @OneToMany(mappedBy="pedido",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<DetallePedido> detalles=new ArrayList<>();
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }
}

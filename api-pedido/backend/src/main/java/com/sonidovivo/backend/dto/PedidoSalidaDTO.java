package com.sonidovivo.backend.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record PedidoSalidaDTO(Long idPedido, Long idUsuario, LocalDateTime fechaPedido,
    BigDecimal total, String estadoSeguimiento, String direccionEnvio,
    String codigoSeguimiento, List<Detalle> detalles) {
    public record Detalle(Long idDetalle, Long idProducto, Integer cantidad, BigDecimal precioUnitario) {}
}

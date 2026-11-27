package com.sonidovivo.backend.service;
import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.model.*;
import com.sonidovivo.backend.repository.*;

import com.sonidovivo.backend.exception.RecursoNoEncontradoException;
import com.sonidovivo.backend.exception.ConflictoException;
import com.sonidovivo.backend.exception.SolicitudInvalidaException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final ProductoRepository productos;

    public PedidoService(
            PedidoRepository pedidos,
            ProductoRepository productos) {

        this.pedidos = pedidos;
        this.productos = productos;
    }

    // Convertir entidad a DTO de salida
    private PedidoSalidaDTO salida(Pedido p) {

        var detalles = p.getDetalles().stream()
                .sorted(Comparator.comparing(
                        d -> d.getProducto().getIdProducto()))
                .map(d -> new PedidoSalidaDTO.Detalle(
                        d.getIdDetalle(),
                        d.getProducto().getIdProducto(),
                        d.getCantidad(),
                        d.getPrecioUnitario()
                ))
                .toList();

        return new PedidoSalidaDTO(
                p.getIdPedido(),
                p.getIdUsuario(),
                p.getFechaPedido(),
                p.getTotal(),
                p.getEstadoSeguimiento(),
                p.getDireccionEnvio(),
                p.getCodigoSeguimiento(),
                detalles
        );
    }

    // Buscar pedido perteneciente al usuario
    private Pedido buscar(long id, long usuario, boolean lock) {

        return (lock
                ? pedidos.bloquear(id, usuario)
                : pedidos.findByIdPedidoAndIdUsuario(id, usuario))

                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Pedido no encontrado"
                        )
                );
    }

    // Buscar y bloquear producto
    private Producto bloquear(long id) {

        return productos.bloquear(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Producto no encontrado: " + id
                        )
                );
    }

    // Listar pedidos del usuario
    @Transactional(readOnly = true)
    public List<PedidoSalidaDTO> listar(long usuario) {

        return pedidos
                .findByIdUsuarioOrderByIdPedidoDesc(usuario)
                .stream()
                .map(this::salida)
                .toList();
    }

    // Obtener un pedido específico
    @Transactional(readOnly = true)
    public PedidoSalidaDTO obtener(long id, long usuario) {

        return salida(buscar(id, usuario, false));
    }

    // Crear pedido
    @Transactional
    public PedidoSalidaDTO crear(
            long usuario,
            PedidoEntradaDTO entrada) {

        var cantidades = new TreeMap<Long, Integer>();

        // Agrupar cantidades de productos repetidos
        for (var d : entrada.detalles()) {

            try {
                cantidades.merge(
                        d.idProducto(),
                        d.cantidad(),
                        Math::addExact
                );
            } catch (ArithmeticException e) {
                throw new SolicitudInvalidaException(
                        "La cantidad solicitada es demasiado grande"
                );
            }
        }

        Pedido pedido = new Pedido();

        pedido.setIdUsuario(usuario);
        pedido.setFechaPedido(
                LocalDateTime.now(ZoneOffset.UTC)
        );
        pedido.setEstadoSeguimiento("PENDIENTE");
        pedido.setDireccionEnvio(
                entrada.direccionEnvio().strip()
        );
        pedido.setCodigoSeguimiento(
                UUID.randomUUID().toString()
        );

        BigDecimal total = BigDecimal.ZERO;

        // Procesar productos del pedido
        for (var e : cantidades.entrySet()) {

            Producto p = bloquear(e.getKey());

            // Validar disponibilidad y stock
            if (!Integer.valueOf(1).equals(p.getActivo())
                    || p.getStock() == null
                    || p.getStock() < e.getValue()) {

                throw new ConflictoException(
                        "Producto inactivo o stock insuficiente: "
                                + e.getKey()
                );
            }

            // Validar precio
            if (p.getPrecio() == null
                    || p.getPrecio().signum() < 0) {

                throw new ConflictoException(
                        "Precio inválido"
                );
            }

            // Crear detalle del pedido
            DetallePedido d = new DetallePedido();

            d.setProducto(p);
            d.setCantidad(e.getValue());
            d.setPrecioUnitario(p.getPrecio());

            pedido.agregarDetalle(d);

            // Calcular total
            total = total.add(
                    p.getPrecio().multiply(
                            BigDecimal.valueOf(e.getValue())
                    )
            );

            // Descontar stock
            p.setStock(p.getStock() - e.getValue());
        }

        // Validar límite del total
        if (total.compareTo(
                new BigDecimal("99999999.99")) > 0) {

            throw new SolicitudInvalidaException(
                    "Total excede el límite de Oracle"
            );
        }

        pedido.setTotal(total);

        // Guardar pedido y devolver DTO
        return salida(pedidos.saveAndFlush(pedido));
    }

    // Eliminar pedido
    @Transactional
    public void eliminar(long id, long usuario) {

        Pedido pedido = buscar(id, usuario, true);

        // Solo eliminar pedidos pendientes
        if (!"PENDIENTE".equals(
                pedido.getEstadoSeguimiento())) {

            throw new ConflictoException(
                    "Solo se pueden eliminar pedidos pendientes"
            );
        }

        var cantidades = new TreeMap<Long, Integer>();

        // Obtener cantidades que deben devolverse al stock
        for (var d : pedido.getDetalles()) {

            cantidades.merge(
                    d.getProducto().getIdProducto(),
                    d.getCantidad(),
                    Math::addExact
            );
        }

        // Devolver stock a los productos
        for (var e : cantidades.entrySet()) {

            Producto p = bloquear(e.getKey());

            p.setStock(
                    Math.addExact(
                            p.getStock(),
                            e.getValue()
                    )
            );
        }

        // Eliminar pedido
        pedidos.delete(pedido);
        pedidos.flush();
    }
}

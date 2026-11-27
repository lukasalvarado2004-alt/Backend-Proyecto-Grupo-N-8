package com.sonidovivo.backend.repository;
import com.sonidovivo.backend.model.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByIdUsuarioOrderByIdPedidoDesc(Long idUsuario);
    Optional<Pedido> findByIdPedidoAndIdUsuario(Long idPedido,Long idUsuario);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.idPedido = :id and p.idUsuario = :usuario")
    Optional<Pedido> bloquear(@org.springframework.data.repository.query.Param("id") Long id,
                               @org.springframework.data.repository.query.Param("usuario") Long usuario);
}

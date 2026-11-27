package com.sonidovivo.backend.controller;
import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name="Pedidos")
@SecurityRequirement(name="bearerAuth")
public class PedidoController {
    private final PedidoService service;
    public PedidoController(PedidoService service) { this.service=service; }
    private long usuario(Jwt jwt) { return Long.parseLong(jwt.getSubject()); }
    @GetMapping @Operation(summary="Listar mis pedidos")

    public List<PedidoSalidaDTO> listar(@AuthenticationPrincipal Jwt jwt) { return service.listar(usuario(jwt)); }
    @GetMapping("/{id}") @Operation(summary="Consultar mi pedido")

    public PedidoSalidaDTO buscar(@PathVariable long id,@AuthenticationPrincipal Jwt jwt) { return service.obtener(id,usuario(jwt)); }
    @PostMapping @Operation(summary="Crear pedido con sus productos")
    
    public ResponseEntity<PedidoSalidaDTO> crear(@Valid @RequestBody PedidoEntradaDTO entrada,@AuthenticationPrincipal Jwt jwt) {
        var p=service.crear(usuario(jwt),entrada);
        return ResponseEntity.created(URI.create("/api/v1/pedidos/"+p.idPedido())).body(p);
    }
    @DeleteMapping("/{id}") @Operation(summary="Eliminar mi pedido pendiente y devolver stock")
    public ResponseEntity<Void> eliminar(@PathVariable long id,@AuthenticationPrincipal Jwt jwt) { service.eliminar(id,usuario(jwt)); return ResponseEntity.noContent().build(); }
}

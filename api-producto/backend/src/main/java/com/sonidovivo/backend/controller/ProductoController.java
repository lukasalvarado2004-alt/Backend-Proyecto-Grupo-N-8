package com.sonidovivo.backend.controller;

import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productos;

    @GetMapping
    public List<ProductoSalidaDTO> listar() {
        return productos.listarActivos();
    }

    @GetMapping("/{id}")
    public ProductoSalidaDTO buscar(@PathVariable Long id) {
        return productos.buscarPorId(id);
    }

    @GetMapping("/categoria/{id}")
    public List<ProductoSalidaDTO> listarPorCategoria(@PathVariable Long id) {
        return productos.listarPorCategoria(id);
    }

    @Operation(summary = "Crear producto", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ProductoSalidaDTO> crear(@Valid @RequestBody ProductoEntradaDTO d,
                                                   @AuthenticationPrincipal Jwt actor) {
        var creado = productos.crear(d, actor);
        return ResponseEntity.created(URI.create("/api/v1/productos/" + creado.id())).body(creado);
    }

    @Operation(summary = "Actualizar producto", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ProductoSalidaDTO actualizar(@PathVariable Long id, @Valid @RequestBody ProductoEntradaDTO d,
                                         @AuthenticationPrincipal Jwt actor) {
        return productos.actualizar(id, d, actor);
    }

    @Operation(summary = "Eliminar producto", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt actor) {
        productos.eliminar(id, actor);
        return ResponseEntity.noContent().build();
    }
}

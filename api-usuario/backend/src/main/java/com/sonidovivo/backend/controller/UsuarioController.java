package com.sonidovivo.backend.controller;

import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarios;

    @Operation(summary = "Registrar cliente", responses = {
        @ApiResponse(responseCode = "201", description = "Creado"),
        @ApiResponse(responseCode = "400", description = "Entrada inválida"),
        @ApiResponse(responseCode = "409", description = "Correo duplicado")
    })
    @PostMapping
    public ResponseEntity<UsuarioSalidaDTO> crear(@Valid @RequestBody UsuarioEntradaDTO d) {
        var creado = usuarios.crear(d);
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + creado.getId()))
                .body(creado);
    }

    @Operation(summary = "Listar usuarios · administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public List<UsuarioSalidaDTO> listar() {
        return usuarios.listar();
    }

    @Operation(summary = "Consultar cuenta · titular o administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public UsuarioSalidaDTO buscar(@PathVariable Long id, @AuthenticationPrincipal Jwt actor) {
        return usuarios.buscar(id, actor);
    }

    @Operation(summary = "Buscar por correo · administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/email/{email:.+}")
    public UsuarioSalidaDTO buscarPorEmail(@PathVariable String email) {
        return usuarios.buscarPorEmail(email);
    }

    @Operation(summary = "Editar datos · titular o administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public UsuarioSalidaDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizacionDTO d,
                                      @AuthenticationPrincipal Jwt actor) {
        return usuarios.actualizar(id, d, actor);
    }

    @Operation(summary = "Eliminar usuario · administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt actor) {
        usuarios.eliminar(id, actor);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar rol · administrador", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/rol")
    public UsuarioSalidaDTO rol(@PathVariable Long id, @Valid @RequestBody CambioRolDTO d,
                               @AuthenticationPrincipal Jwt actor) {
        return usuarios.cambiarRol(id, d.rol(), actor);
    }
}

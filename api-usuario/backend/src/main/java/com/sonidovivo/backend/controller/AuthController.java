package com.sonidovivo.backend.controller;

import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.service.AuthService;
import com.sonidovivo.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;
    private final UsuarioService usuarios;

    @Operation(summary = "Iniciar sesión y obtener un JWT de 15 minutos",
        responses = {@ApiResponse(responseCode = "200", description = "Sesión iniciada"),
                     @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")})
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO entrada) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(auth.login(entrada));
    }

    @Operation(summary = "Consultar mi perfil",
               security = @SecurityRequirement(name = "bearerAuth"),
               responses = {
                   @ApiResponse(responseCode = "200", description = "Perfil del usuario autenticado"),
                   @ApiResponse(responseCode = "401", description = "Token ausente o inválido")
               })
    @GetMapping("/me")
    public ResponseEntity<UsuarioSalidaDTO> me(@AuthenticationPrincipal Jwt actor) {
        Long id = Long.valueOf(actor.getSubject());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(usuarios.buscar(id, actor));
    }
}

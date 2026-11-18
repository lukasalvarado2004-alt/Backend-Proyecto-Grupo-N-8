
package com.sonidovivo.backend.controller;

import com.sonidovivo.backend.dto.UsuarioEntradaDTO;
import com.sonidovivo.backend.dto.UsuarioSalidaDTO;
import com.sonidovivo.backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Usuarios", description = "Gestion de usuarios")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(summary = "Listar usuarios",
            description = "Lista todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public List<UsuarioSalidaDTO> listar() {
        return service.listar();
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    public UsuarioSalidaDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Buscar usuario por correo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/email/{email:.+}")
    public UsuarioSalidaDTO buscarPorEmail(
            @PathVariable String email) {

        return service.buscarPorEmail(email);
    }

    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "409", description = "Correo registrado")
    })
    @PostMapping
    public ResponseEntity<UsuarioSalidaDTO> crear(
            @Valid @RequestBody UsuarioEntradaDTO entrada) {

        UsuarioSalidaDTO creado = service.crear(entrada);

        return ResponseEntity.created(
                URI.create("/api/v1/usuarios/" + creado.getId())
        ).body(creado);
    }

    @Operation(summary = "Actualizar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PutMapping("/{id}")
    public UsuarioSalidaDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioEntradaDTO entrada) {

        return service.actualizar(id, entrada);
    }

    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}

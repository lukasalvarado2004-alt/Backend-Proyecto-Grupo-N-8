
package com.sonidovivo.backend.controller;

import com.sonidovivo.backend.model.Usuario;
import com.sonidovivo.backend.service.UsuarioService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    public record UsuarioRespuesta(
        Long id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        String rol
    ) {}

    public record RegistroUsuario(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank String password,
        String telefono,
        String direccion
    ) {}

    public record ActualizarUsuario(
        @NotBlank String nombre,
        String telefono,
        String direccion
    ) {}

    private UsuarioRespuesta convertir(Usuario u) {
        return new UsuarioRespuesta(
            u.getIdUsuario(),
            u.getNombre(),
            u.getEmail(),
            u.getTelefono(),
            u.getDireccion(),
            u.getRol()
        );
    }

    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return service.listar().stream()
            .map(this::convertir)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuesta> buscar(
            @PathVariable Long id) {

        return service.buscarPorId(id)
            .map(u -> ResponseEntity.ok(convertir(u)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioRespuesta> crear(
            @Valid @RequestBody RegistroUsuario datos) {

        Usuario usuario = service.guardar(
            datos.nombre(),
            datos.email(),
            datos.password(),
            datos.telefono(),
            datos.direccion()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(convertir(usuario));
    }

    @PutMapping("/{id}")
    public UsuarioRespuesta actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuario datos) {

        return convertir(service.actualizar(
            id,
            datos.nombre(),
            datos.telefono(),
            datos.direccion()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

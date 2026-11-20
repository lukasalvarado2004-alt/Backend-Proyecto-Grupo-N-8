package com.sonidovivo.backend.service;

import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.exception.RecursoNoEncontradoException;
import com.sonidovivo.backend.model.*;
import com.sonidovivo.backend.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwords;

    public List<UsuarioSalidaDTO> listar() {
        return usuarios.findAll().stream().map(this::salida).toList();
    }

    public UsuarioSalidaDTO buscar(Long id, Jwt actor) {
        comprobarPropietario(id, actor);
        return salida(obtener(id));
    }

    public UsuarioSalidaDTO buscarPorEmail(String email) {
        return salida(usuarios.findByEmail(email.strip())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado.")));
    }

    @Transactional
    public UsuarioSalidaDTO crear(UsuarioEntradaDTO d) {
        if (d.contrasena().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contraseña supera 72 bytes en UTF-8.");
        }
        if (usuarios.existsByEmail(d.correo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El correo ya está registrado.");
        }
        var u = new Usuario();
        u.setNombre(d.nombre());
        u.setEmail(d.correo());
        u.setPasswordHash(passwords.encode(d.contrasena()));
        u.setTelefono(d.telefono());
        u.setDireccion(d.direccion());
        u.setRol(Rol.CLIENTE.name());
        u.setFechaRegistro(LocalDateTime.now());
        return salida(usuarios.saveAndFlush(u));
    }

    @Transactional
    public UsuarioSalidaDTO actualizar(Long id, UsuarioActualizacionDTO d, Jwt actor) {
        comprobarPropietario(id, actor);
        var u = obtener(id);
        u.setNombre(d.nombre());
        u.setTelefono(d.telefono());
        u.setDireccion(d.direccion());
        return salida(u);
    }

    @Transactional
    public void eliminar(Long id, Jwt actor) {
        comprobarOtraCuenta(id, actor);
        usuarios.delete(obtener(id));
    }

    @Transactional
    public UsuarioSalidaDTO cambiarRol(Long id, Rol rol, Jwt actor) {
        comprobarOtraCuenta(id, actor);
        var u = obtener(id);
        u.setRol(rol.name());
        return salida(u);
    }

    private void comprobarOtraCuenta(Long id, Jwt actor) {
        if (id.toString().equals(actor.getSubject())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No puedes eliminar ni cambiar el rol de tu propia cuenta.");
        }
    }

    private void comprobarPropietario(Long id, Jwt actor) {
        boolean admin = Rol.ADMINISTRADOR.name().equals(actor.getClaimAsString("rol"));
        if (!admin && !id.toString().equals(actor.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado.");
        }
    }

    private Usuario obtener(Long id) {
        return usuarios.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Usuario no encontrado."));
    }

    private UsuarioSalidaDTO salida(Usuario u) {
        return new UsuarioSalidaDTO(u.getIdUsuario(), u.getNombre(), u.getEmail(),
                u.getTelefono(), u.getDireccion(), u.getRol(), u.getFechaRegistro());
    }
}

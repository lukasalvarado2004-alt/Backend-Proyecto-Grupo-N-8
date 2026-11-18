
package com.sonidovivo.backend.service;

import com.sonidovivo.backend.dto.UsuarioEntradaDTO;
import com.sonidovivo.backend.dto.UsuarioSalidaDTO;
import com.sonidovivo.backend.model.Usuario;
import com.sonidovivo.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    // Listar todos los usuarios
    public List<UsuarioSalidaDTO> listar() {
        return repository.findAll()
                .stream()
                .map(this::aSalida)
                .toList();
    }

    // Buscar usuario por ID
    public UsuarioSalidaDTO buscar(Long id) {
        return aSalida(obtener(id));
    }

    // Buscar usuario por correo
    public UsuarioSalidaDTO buscarPorEmail(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        return aSalida(usuario);
    }

    // Crear usuario
    public UsuarioSalidaDTO crear(UsuarioEntradaDTO entrada) {

        if (repository.existsByEmail(entrada.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya esta registrado"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(entrada.getNombre());
        usuario.setEmail(entrada.getCorreo());
        usuario.setPasswordHash(entrada.getContrasena());
        usuario.setTelefono(entrada.getTelefono());
        usuario.setDireccion(entrada.getDireccion());

        usuario.setRol("CLIENTE");
        usuario.setFechaRegistro(LocalDateTime.now());

        return aSalida(repository.save(usuario));
    }

    // Actualizar usuario
    public UsuarioSalidaDTO actualizar(
            Long id, UsuarioEntradaDTO entrada) {

        Usuario usuario = obtener(id);

        usuario.setNombre(entrada.getNombre());
        usuario.setTelefono(entrada.getTelefono());
        usuario.setDireccion(entrada.getDireccion());

        return aSalida(repository.save(usuario));
    }

    // Eliminar usuario por ID
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado"
            );
        }

        repository.deleteById(id);
    }

    // Obtener entidad Usuario
    private Usuario obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    // Convertir entidad a DTO de salida
    private UsuarioSalidaDTO aSalida(Usuario usuario) {
        return new UsuarioSalidaDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRol(),
                usuario.getFechaRegistro()
        );
    }
}

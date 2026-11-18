
package com.sonidovivo.backend.service;

import com.sonidovivo.backend.model.Usuario;
import com.sonidovivo.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
  
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }

   public Usuario guardar(
        String nombre,
        String email,
        String password,
        String telefono,
        String direccion) {

    if (repository.existsByEmail(email)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "El correo ya esta registrado"
        );
    }

    Usuario usuario = new Usuario();

    usuario.setNombre(nombre);
    usuario.setEmail(email);
    usuario.setPasswordHash(password);
    usuario.setTelefono(telefono);
    usuario.setDireccion(direccion);
    usuario.setRol("CLIENTE");
    usuario.setFechaRegistro(LocalDateTime.now());

    return repository.save(usuario);
}

    public Usuario actualizar(
            Long id,
            String nombre,
            String telefono,
            String direccion) {

        Usuario usuario = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario no encontrado"
            ));

        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);

        return repository.save(usuario);
    }

    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario no encontrado"
            );
        }

        repository.deleteById(id);
    }
}

package com.sonidovivo.backend.repository;
import com.sonidovivo.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {}

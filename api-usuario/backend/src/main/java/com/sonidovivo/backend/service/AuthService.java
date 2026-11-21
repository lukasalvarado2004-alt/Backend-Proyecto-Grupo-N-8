package com.sonidovivo.backend.service;

import com.sonidovivo.backend.dto.*;
import com.sonidovivo.backend.model.Rol;
import com.sonidovivo.backend.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private static final long VIGENCIA_SEGUNDOS = 900;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwt;
    private final String emisor;
    private final String audiencia;
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwords,
                       JwtEncoder jwt, @Value("${app.jwt.emisor}") String emisor,
                       @Value("${app.jwt.audiencia}") String audiencia) {
        this.usuarios = usuarios;
        this.passwords = passwords;
        this.jwt = jwt;
        this.emisor = emisor;
        this.audiencia = audiencia;
        this.hashFicticio = passwords.encode(UUID.randomUUID().toString());
    }

    public TokenDTO login(LoginDTO datos) {
        var usuario = usuarios.findByEmail(datos.correo().strip());
        String hash = usuario.map(u -> u.getPasswordHash()).orElse("");
        boolean bcrypt = hash.matches("[$]2[aby][$][0-9]{2}[$][./A-Za-z0-9]{53}");
        boolean valido = datos.contrasena().getBytes(StandardCharsets.UTF_8).length <= 72
                && passwords.matches(datos.contrasena(), bcrypt ? hash : hashFicticio);
        if (!valido || !bcrypt || usuario.isEmpty()
                || !Rol.reconocido(usuario.get().getRol())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas.");
        }
        var cuenta = usuario.orElseThrow();
        var ahora = Instant.now();
        var claims = JwtClaimsSet.builder().issuer(emisor).issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(VIGENCIA_SEGUNDOS))
                .subject(cuenta.getIdUsuario().toString())
                .audience(List.of(audiencia))
                .claim("rol", cuenta.getRol()).build();
        var encabezado = JwsHeader.with(MacAlgorithm.HS256).build();
        var token = jwt.encode(JwtEncoderParameters.from(encabezado, claims));
        return new TokenDTO(token.getTokenValue(), "Bearer", VIGENCIA_SEGUNDOS,
                cuenta.getIdUsuario(), cuenta.getRol());
    }
}

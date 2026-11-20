package com.sonidovivo.backend.config;

import com.sonidovivo.backend.repository.UsuarioRepository;
import com.sonidovivo.backend.model.Rol;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import java.time.Duration;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secreto-base64}") String base64) {
        byte[] clave;
        try {
            clave = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT_SECRET debe estar codificado en Base64.", e);
        }
        if (clave.length < 32) {
            throw new IllegalStateException("JWT_SECRET requiere 32 bytes aleatorios");
        }
        return new SecretKeySpec(clave, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey clave) {
        return NimbusJwtEncoder.withSecretKey(clave)
                .algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey clave, UsuarioRepository usuarios,
                          @Value("${app.jwt.emisor}") String emisor) {
        var decoder = NimbusJwtDecoder.withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256).build();
        var tiempo = new JwtTimestampValidator(Duration.ZERO);
        var emisorValido = new JwtIssuerValidator(emisor);
        decoder.setJwtValidator(jwt -> {
            var validacion = new DelegatingOAuth2TokenValidator<>(
                    tiempo, emisorValido).validate(jwt);
            if (validacion.hasErrors()) return validacion;
            if (jwt.getExpiresAt() == null || jwt.getSubject() == null) {
                return invalido();
            }
            try {
                return usuarios.findById(Long.valueOf(jwt.getSubject()))
                        .filter(u -> Rol.reconocido(u.getRol()))
                        .filter(u -> Objects.equals(u.getRol(), jwt.getClaimAsString("rol")))
                        .map(u -> OAuth2TokenValidatorResult.success())
                        .orElseGet(SecurityConfig::invalido);
            } catch (NumberFormatException e) {
                return invalido();
            }
        });
        return decoder;
    }

    private static OAuth2TokenValidatorResult invalido() {
        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Sesión inválida.", null));
    }

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);

        return http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                 "/v3/api-docs", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login",
                                 "/api/v1/usuarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/usuarios",
                                 "/api/v1/usuarios/email/**")
                    .hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/{id}")
                    .hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/usuarios/{id}/rol")
                    .hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/{id}")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")
                .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/{id}")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")
                .anyRequest().denyAll())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
            .build();
    }
}

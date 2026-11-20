package com.sonidovivo.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDTO(
    String token, String tipo, long expiraEnSegundos,
    Long usuarioId, String rol
) {

    @JsonProperty("accessToken")
    public String accessToken() {
        return token;
    }

    @Override
    public String toString() {
        return "TokenDTO[datos protegidos]";
    }
}

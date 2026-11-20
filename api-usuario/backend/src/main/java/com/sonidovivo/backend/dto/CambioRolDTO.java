package com.sonidovivo.backend.dto;

import com.sonidovivo.backend.model.Rol;
import jakarta.validation.constraints.NotNull;

public record CambioRolDTO(@NotNull Rol rol) {}

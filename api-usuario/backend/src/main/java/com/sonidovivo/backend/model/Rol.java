package com.sonidovivo.backend.model;

public enum Rol {
    ADMINISTRADOR, VENDEDOR, CLIENTE;

    public static boolean reconocido(String valor) {
        return ADMINISTRADOR.name().equals(valor)
                || VENDEDOR.name().equals(valor)
                || CLIENTE.name().equals(valor);
    }
}

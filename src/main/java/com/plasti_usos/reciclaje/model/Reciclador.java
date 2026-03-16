package com.plasti_usos.reciclaje.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RECICLADOR")
public class Reciclador extends Usuario {
    private Integer saldoPuntos = 0;

    @Override
    public List<String> obtenerPermisos() {
        return List.of("ENTREGAR_RESIDUOS", "CANJEAR_PUNTOS", "VER_MAPA");
    }

    @Override
    public String obtenerTablero() {
        return "VISTA_RECICLADOR: Saldo = " + this.saldoPuntos;
    }
}
package com.plasti_usos.reciclaje.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {
    private Integer nivelPrivilegios = 1;

    @Override
    public List<String> obtenerPermisos() {
        return List.of("MODIFICAR_STOCK", "BORRAR_USUARIOS", "VER_METRICAS_CIUDAD", "EDITAR_PUNTOS");
    }

    @Override
    public String obtenerTablero() {
        return "Panel Maestro de Popayán - Privilegios nivel: " + this.nivelPrivilegios;
    }
}
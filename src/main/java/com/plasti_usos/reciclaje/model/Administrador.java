package com.plasti_usos.reciclaje.model;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {
    private Integer nivelPrivilegios = 0;

    @Override
    public List<String> obtenerPermisos() {
        return List.of("GESTIONAR_PUNTOS", "GESTIONAR_USUARIOS", "VER_REPORTES");
    }

    @Override
    public String obtenerTablero() {
        return "VISTA_ADMINISTRADOR: Nivel de acceso " + this.nivelPrivilegios;
    }
}
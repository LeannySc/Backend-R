package com.plasti_usos.reciclaje.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("ENCARGADO")
public class EncargadoPunto extends Usuario {
    @Override
    public List<String> obtenerPermisos() {
        return List.of("VALIDAR_ENTREGA", "GESTIONAR_PUNTO");
    }

    @Override
    public String obtenerTablero() {
        return "Gestión del Punto ID: " + this.getPuntoID();
    }
}
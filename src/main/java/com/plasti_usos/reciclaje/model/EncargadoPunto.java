package com.plasti_usos.reciclaje.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) // Para incluir los campos de Usuario en equals y hashCode
@DiscriminatorValue("ENCARGADO")
public class EncargadoPunto extends Usuario {

    private Long puntoID; // Se llena solo si es Encargado
    private LocalDate asignadoE = LocalDate.now();

    @Override
    public List<String> obtenerPermisos() {
        return List.of("VALIDAR_ENTREGA", "RECHAZAR_ENTREGA", "VER_DASHBOARD_PUNTO");
    }

    @Override
    public String obtenerTablero() {
        return "Panel de Recolector - Punto ID: " + this.getPuntoID();
    }
}
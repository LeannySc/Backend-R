package com.plasti_usos.reciclaje.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) // Para incluir los campos de Usuario en equals y hashCode
@DiscriminatorValue("RECICLADOR")
public class Reciclador extends Usuario {

    private Integer saldoPuntos = 0;

    @OneToMany(mappedBy = "reciclador", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "reciclador", "punto", "detalles" })
    private List<TransaccionEntrega> historialEntrega = new ArrayList<>();

    @Override
    public List<String> obtenerPermisos() {
        return List.of("VER_MAPA", "REGISTRAR_ENTREGA", "CONSULTAR_CANJE");
    }

    @Override
    public String obtenerTablero() {
        return "Dashboard de Ciudadano: " + this.getSaldoPuntos() + " puntos acumulados";
    }

    public int consultarPuntos() {
        return this.saldoPuntos;
    }

    public List<TransaccionEntrega> verHistorial() {
        return this.historialEntrega;
    }

}
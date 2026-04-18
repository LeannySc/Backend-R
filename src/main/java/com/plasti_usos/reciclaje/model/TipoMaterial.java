package com.plasti_usos.reciclaje.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class TipoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String nombre; 
    private float puntosPorUnidad; 

    public float getPuntos() {
        return this.puntosPorUnidad;
    }

    @ManyToOne
    @JoinColumn(name = "punto_recoleccion_id") 
    @JsonIgnore
    private PuntoRecoleccion punto;

}

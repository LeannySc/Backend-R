package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class TipoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String nombre; // Ej: PET, HDPE, etc.
    private float puntosPorUnidad; // Puntos otorgados por cada unidad de este material

    public float getPuntos() {
        return this.puntosPorUnidad;
    }

    @ManyToOne
    @JoinColumn(name = "punto_recoleccion_id") // Clave foránea a PuntoRecoleccion
    private PuntoRecoleccion punto;

}

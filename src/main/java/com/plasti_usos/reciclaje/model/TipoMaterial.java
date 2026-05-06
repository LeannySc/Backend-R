package com.plasti_usos.reciclaje.model;

import java.util.HashSet;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class TipoMaterial {

    @ManyToMany(mappedBy = "materiales") // ✅ Cambiado de ManyToOne a ManyToMany
    // @JsonIgnore
    @JsonBackReference
    @ToString.Exclude
    private Set<PuntoRecoleccion> puntos = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private float puntosPorUnidad;

    public float getPuntos() {
        return this.puntosPorUnidad;
    }

    // @ManyToOne
    // @JoinColumn(name = "punto_recoleccion_id")
    // @JsonIgnore
    // private PuntoRecoleccion punto;

}

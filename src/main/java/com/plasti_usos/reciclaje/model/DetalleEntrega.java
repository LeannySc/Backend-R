package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DetalleEntrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TipoMaterial material; // Para saber si es PET, HDPE, etc.

    private float cantidad;
    private int puntosOtorgados;
}
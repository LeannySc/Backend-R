package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductoMaravilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private int costoPuntos;
    private int stock;
    private String imagenUrl;
}
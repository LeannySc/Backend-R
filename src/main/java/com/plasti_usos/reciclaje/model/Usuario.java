package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED) // Para soportar la familia de clases del UML
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String correo;
    private String contrasena;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Campos condicionales según el tipo de producto de la Fábrica
    private Integer saldoPuntos; // Se llena solo si es Reciclador
    private Long puntoID; // Se llena solo si es Encargado
}
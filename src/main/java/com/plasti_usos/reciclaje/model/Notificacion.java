package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private String subtexto;

    @Enumerated(EnumType.STRING)
    private Rol rolDestino;

    private String claseOrigen; // Ej: "TransaccionService.java"
    private boolean leido = false;
    private boolean conEmail = false;
    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
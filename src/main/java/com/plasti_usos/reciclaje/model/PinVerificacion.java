package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class PinVerificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private LocalDateTime fechaExpiracion;
    private boolean usado = false;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Relación con el operario
}


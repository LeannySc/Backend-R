package com.plasti_usos.reciclaje.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PedidoCanje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario reciclador;

    @ManyToOne
    private ProductoMaravilla producto;

    private String direccionEnvio;
    private String estado = "SOLICITADO";
    private LocalDateTime fechaPedido = LocalDateTime.now();

    public void confirmar() {
        this.estado = "ENTREGADO";
    }
}

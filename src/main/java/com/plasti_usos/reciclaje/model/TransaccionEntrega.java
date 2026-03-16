package com.plasti_usos.reciclaje.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class TransaccionEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado = EstadoTransaccion.PENDIENTE; // Por defecto, la transacción inicia como pendiente

    @ManyToOne
    @JsonIgnore
    private Reciclador reciclador;

    @ManyToOne // Una transaccion ocurre en un Punto de Recolección o fisico
    private PuntoRecoleccion punto;

    private double cantidadKilos;
    private int puntosOtorgados;
    private LocalDateTime fechaEntrega = LocalDateTime.now();

}

package com.plasti_usos.reciclaje.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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

    @ManyToOne // Una Transaccion pertenece a un Usuario (Reciclador)
    private Usuario reciclador;

    @ManyToOne // Una transaccion ocurre en un Punto de Recolección o fisico
    private PuntoRecoleccion punto;

    private double cantidadKilos;
    private int puntosOtorgados;
    private LocalDateTime fechaEntrega = LocalDateTime.now();

}

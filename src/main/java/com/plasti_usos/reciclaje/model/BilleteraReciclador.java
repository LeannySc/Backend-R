package com.plasti_usos.reciclaje.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BilleteraReciclador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer saldoPuntos = 0;
    private String nivelEco = "Bronce"; // Ej: Bronce, Plata, Oro
    private Double kilosAportados = 0.0;

    @JoinColumn(name = "reciclador_id")
    @OneToOne(mappedBy = "billetera")
    @JsonIgnore
    private Reciclador reciclador; // Vínculo 1 a 1 con el ciudadano
}
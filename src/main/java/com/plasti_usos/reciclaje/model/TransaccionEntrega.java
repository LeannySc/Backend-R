package com.plasti_usos.reciclaje.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//import org.hibernate.engine.internal.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transaccion_id") // Clave foránea en DetalleEntrega
    private List<DetalleEntrega> detalles = new ArrayList<>(); // Lista de materiales entregados con sus cantidades y
                                                               // puntos otorgados

    private double cantidadKilos;
    private int puntosOtorgados;
    private LocalDateTime fechaEntrega = LocalDateTime.now();

}

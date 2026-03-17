package com.plasti_usos.reciclaje.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class PuntoRecoleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean activo = true; // Para saber si el punto está operativo o no
    private String nombre;
    private String direccion;
    private String codigoQR; // Para validar la entrega física
    // PuntoRecoleccion.java
    private Double latitud; // Usa D mayúscula
    private Double longitud; // Usa D mayúscula
    private double nivelLlenado = 0.0; // Porcentaje de llenado del contenedor
    private double capacidadMaximakg = 50.0; // Capacidad máxima en kg del contenedor
    private String estadoBote = "VACÍO"; // VACÍO, MEDIO, LLENO
    private boolean necesitaRecoleccion = false;

    @OneToMany(mappedBy = "punto", fetch = FetchType.LAZY)
    private List<TipoMaterial> materiales; // Para saber qué tipos de materiales se pueden entregar en este punto

    public List<String> obtenerMateriales() {
        return materiales.stream().map(TipoMaterial::getNombre).toList();
    }

    public void activar() {
        this.activo = true;
    }

    public void actualizarEstado() {
        this.necesitaRecoleccion = (this.nivelLlenado > 85.0);
    }

}
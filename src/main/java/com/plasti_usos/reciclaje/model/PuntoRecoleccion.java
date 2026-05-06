package com.plasti_usos.reciclaje.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class PuntoRecoleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean activo = true;
    private String nombre;
    private String direccion;
    private String codigoQR;
    private Double latitud;
    private Double longitud;
    private double nivelLlenado = 0.0;
    private double capacidadMaximakg = 50.0;
    private String estadoBote = "VACÍO";
    private boolean necesitaRecoleccion = false;
    private boolean ocupado = false;
    private Long encargadoEnCaminoId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "punto_materiales", joinColumns = @JoinColumn(name = "punto_id"), inverseJoinColumns = @JoinColumn(name = "material_id"))
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private Set<TipoMaterial> materiales = new HashSet<>();

    public List<String> obtenerMateriales() {
        return materiales.stream().map(TipoMaterial::getNombre).toList();
    }

    public void activar() {
        this.activo = true;
    }

    public void actualizarEstado() {
        this.necesitaRecoleccion = (this.nivelLlenado > 85.0);
    }

    public void reportarVaciado() {
        this.nivelLlenado = 0.0;
        this.estadoBote = "VACÍO";
        this.ocupado = false;
        this.encargadoEnCaminoId = null;
        this.necesitaRecoleccion = false;
    }

}
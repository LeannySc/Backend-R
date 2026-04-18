package com.plasti_usos.reciclaje.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @OneToMany(mappedBy = "punto", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("punto")
    private List<TipoMaterial> materiales;
    
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
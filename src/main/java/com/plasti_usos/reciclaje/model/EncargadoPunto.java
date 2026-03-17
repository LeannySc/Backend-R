package com.plasti_usos.reciclaje.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true) // Para incluir los campos de Usuario en equals y hashCode
@DiscriminatorValue("ENCARGADO")
public class EncargadoPunto extends Usuario {

    private LocalDateTime asignadoEn = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "punto_recoleccion_id") // Clave foránea a PuntoRecoleccion
    private PuntoRecoleccion puntoAsignado;

    private Long puntoID; // Se llena solo si es Encargado
    private LocalDate asignadoEl = LocalDate.now();

    @Override
    public List<String> obtenerPermisos() {
        return List.of("VALIDAR_KILOS", "GENERAR_QR", "CERRAR_SEDE");
    }

    @Override
    public String obtenerTablero() {
        if (puntoAsignado != null) {
            return "Estacion: " + puntoAsignado.getNombre() + "| Estado: "
                    + (puntoAsignado.isActivo() ? "VIGILANTE" : "CERRADO");
        }
        return "Sede No Asignada";
    }

}
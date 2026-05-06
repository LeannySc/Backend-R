package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ENCARGADO")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class EncargadoPunto extends Usuario {

    // ✅ NUEVO: Datos para GPS
    private Double latitudActual;
    private Double longitudActual;

    // ✅ NUEVO: Estadísticas de trabajo
    private Integer botesVaciosTotales = 0;
    private LocalDateTime ultimoVaciado = LocalDateTime.now();

    // 🚩 ELIMINADOS: puntoAsignado e historialAsignaciones
    // (Ya no existen en el modelo móvil V2.2)

    @Override
    public List<String> obtenerPermisos() {
        return List.of("RECIBIR_ALERTAS_GPS", "VALIDAR_KILOS_PRESENCIAL", "PROTOCOLOS_VACIADO_MOVIL");
    }

    @Override
    public String obtenerTablero() {
        return "🛰️ UNIDAD MÓVIL GTI - Botes recolectados: " + this.botesVaciosTotales +
                " | Posición: " + (latitudActual != null ? "ACTIVA" : "SIN GPS");
    }
}
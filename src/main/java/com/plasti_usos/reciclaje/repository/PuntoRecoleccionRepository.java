package com.plasti_usos.reciclaje.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import java.util.Optional;

public interface PuntoRecoleccionRepository extends JpaRepository<PuntoRecoleccion, Long> {
    Optional<PuntoRecoleccion> findByEncargadoEnCaminoIdAndOcupadoTrue(Long encargadoId);
}

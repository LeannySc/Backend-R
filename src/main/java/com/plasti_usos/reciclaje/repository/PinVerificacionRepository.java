package com.plasti_usos.reciclaje.repository;

import com.plasti_usos.reciclaje.model.PinVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PinVerificacionRepository extends JpaRepository<PinVerificacion, Long> {
    Optional<PinVerificacion> findByCodigo(String codigo);

    void deleteByUsuarioId(Long usuarioId); // Para limpiar despues de usarlo
}
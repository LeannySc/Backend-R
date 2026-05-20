package com.plasti_usos.reciclaje.repository;

import com.plasti_usos.reciclaje.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    long countByUsuarioIdAndLeidoFalse(Long usuarioId);
}
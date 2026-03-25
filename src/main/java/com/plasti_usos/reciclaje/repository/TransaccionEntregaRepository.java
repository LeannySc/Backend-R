package com.plasti_usos.reciclaje.repository;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransaccionEntregaRepository extends JpaRepository<TransaccionEntrega, Long> {

    @Query("SELECT SUM(t.cantidadKilos) FROM TransaccionEntrega t WHERE t.estado = 'VALIDADA'")
    Double sumarkilosTotalesValidados();
}
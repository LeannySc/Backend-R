package com.plasti_usos.reciclaje.repository;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransaccionEntregaRepository extends JpaRepository<TransaccionEntrega, Long> {

    @Query("SELECT SUM(t.cantidadKilos) FROM TransaccionEntrega t WHERE t.estado = 'VALIDADA'")
    Double sumarkilosTotalesValidados();

    long countByRecicladorId(Long usuarioId);

    @Query(value = "SELECT TO_CHAR(fecha_entrega, 'DD-Mon') as dia, " +
            "SUM(cantidad_kilos) as total_kg " +
            "FROM transaccion_entrega " +
            "WHERE reciclador_usuario_id = :userId " +
            "GROUP BY dia, CAST(fecha_entrega AS DATE) " + // Agrupamos por texto y por fecha real para el orden
            "ORDER BY CAST(fecha_entrega AS DATE) ASC " +
            "LIMIT 7", nativeQuery = true)
    List<Object[]> obtenerKgPorDia(@Param("userId") Long userId);

    @Query(value = "SELECT m.nombre as name, SUM(d.cantidad) as value " +
            "FROM detalle_entrega d " +
            "JOIN tipo_material m ON d.material_id = m.id " +
            "JOIN transaccion_entrega t ON d.transaccion_id = t.id " +
            "WHERE t.reciclador_usuario_id = :userId AND t.estado = 'VALIDADA' " +
            "GROUP BY m.nombre", nativeQuery = true)
    List<Object[]> obtenerDistribucionPorMaterial(@Param("userId") Long userId);

    List<TransaccionEntrega> findTop4ByRecicladorIdOrderByIdDesc(Long recicladorId);

    long countByEncargadoId(Long encargadoId);

    @Query("SELECT SUM(t.cantidadKilos) FROM TransaccionEntrega t WHERE t.encargado.id = :encargadoId")
    Double sumarKgProcesadosPorEncargado(@Param("encargadoId") Long encargadoId);

}
package com.plasti_usos.reciclaje.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plasti_usos.reciclaje.model.PedidoCanje;

public interface PedidoCanjeRepository extends JpaRepository<PedidoCanje, Long> {

    List<PedidoCanje> findByRecicladorId(Long UserId);

}

package com.plasti_usos.reciclaje.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plasti_usos.reciclaje.model.ProductoMaravilla;

public interface ProductoRepository extends JpaRepository<ProductoMaravilla, Long> {

}
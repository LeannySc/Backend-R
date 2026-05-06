package com.plasti_usos.reciclaje.repository;

import com.plasti_usos.reciclaje.model.TipoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMaterialRepository extends JpaRepository<TipoMaterial, Long> {
}
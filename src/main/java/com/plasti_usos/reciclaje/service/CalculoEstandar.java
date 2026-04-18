package com.plasti_usos.reciclaje.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.plasti_usos.reciclaje.model.DetalleEntrega;

@Component
@Primary
public class CalculoEstandar implements CalculadoraPuntos {

    private Map<String, Integer> tarifasBase = new HashMap<>();

    public CalculoEstandar() {
        tarifasBase.put("PET", 10); 
        tarifasBase.put("VIDRIO", 5); 
    }

    @Override
    public int calcular(List<DetalleEntrega> detalles) {

        return (int) detalles.stream()
                .mapToDouble(d -> d.getCantidad() * 10)
                .sum();
    }
}

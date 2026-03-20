package com.plasti_usos.reciclaje.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.plasti_usos.reciclaje.model.DetalleEntrega;

@Component
public class CalculoCampana implements CalculadoraPuntos {

    private float multiplicador = 2.5f;
    //private String campanaActiva = "Campaña de Reciclaje 2026";

    @Override
    public int calcular(List<DetalleEntrega> detalles) {

        double totalkg = detalles.stream().mapToDouble(d -> d.getCantidad()).sum();
        return (int) (totalkg * 10 * multiplicador);
    }

}

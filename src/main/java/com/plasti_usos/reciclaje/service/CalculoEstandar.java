package com.plasti_usos.reciclaje.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CalculoEstandar implements CalculadoraPuntos {
    @Override
    public int calcular(double kilos) {
        return (int) (kilos * 10); // 10 puntos por cada Kg
    }
}
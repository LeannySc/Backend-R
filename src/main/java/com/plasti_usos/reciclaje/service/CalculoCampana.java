package com.plasti_usos.reciclaje.service;

import org.springframework.stereotype.Component;

@Component
public class CalculoCampana implements CalculadoraPuntos {

    @Override
    public int calcular(double kilos) {
        return (int) (kilos * 25); // 25 puntos por cada Kg durante la campaña
    }

}

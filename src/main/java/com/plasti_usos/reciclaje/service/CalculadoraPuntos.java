package com.plasti_usos.reciclaje.service;

import java.util.List;

import com.plasti_usos.reciclaje.model.DetalleEntrega;

public interface CalculadoraPuntos {

    int calcular(List<DetalleEntrega> detalles);

}

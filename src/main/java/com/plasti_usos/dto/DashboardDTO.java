package com.plasti_usos.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DashboardDTO {

    // 1. Stats de las 4 tarjetas superiores
    private String puntos; // stat1 (Verde)
    private String kgReciclados; // stat2 (Azul)
    private String entregas; // stat3 (Amarillo)
    private String canjes; // stat4 (Púrpura - Nivel)

    private String mensajeBienvenida;
    private String descripcionBienvenida;

    // 2. Datos dinámicos para las Gráficas (Líneas 56 y 68 de tu Service)
    private List<Map<String, Object>> historicoMensual;
    private List<Map<String, Object>> distribucionMateriales;
    private List<TransaccionBreve> ultimasActividades; // ✅ Para la dona (lo que faltaba)

    // Datos de la barra de progreso
    private String nombreNivel;
    private int progresoPorcentaje;

    @Data
    public static class TransaccionBreve {
        private String label;
        private String fecha;
        private String puntos;
        private String tipo; 
    }
}
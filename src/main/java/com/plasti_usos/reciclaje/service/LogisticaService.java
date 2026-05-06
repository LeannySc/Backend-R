package com.plasti_usos.reciclaje.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plasti_usos.reciclaje.model.EncargadoPunto;
import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class LogisticaService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PuntoRecoleccionRepository puntoRepo;

    @Transactional
    public void asignarMision(Long puntoId, Long encargadoId) {
        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto no encontrado"));

        punto.setOcupado(true);
        punto.setEncargadoEnCaminoId(encargadoId);
        puntoRepo.save(punto);
    }

    // Fórmula Haversine para calcular distancia entre coordenadas (Km)
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radio de la Tierra
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Buscar operarios en un radio de 5km para un bote lleno
    public List<EncargadoPunto> buscarOperariosCerca(PuntoRecoleccion punto) {
        return usuarioRepo.findAll().stream()
                .filter(u -> u instanceof EncargadoPunto)
                .map(u -> (EncargadoPunto) u)
                .filter(e -> e.getLatitudActual() != null && e.getLongitudActual() != null) // Evitar NPE
                .filter(e -> calcularDistancia(e.getLatitudActual(), e.getLongitudActual(),
                        punto.getLatitud(), punto.getLongitud()) <= 5.0)
                .toList();
    }
}
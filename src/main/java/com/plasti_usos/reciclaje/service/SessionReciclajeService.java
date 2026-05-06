package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionReciclajeService {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private PuntoRecoleccionRepository puntoRepo;

    private ConcurrentHashMap<Long, Long> sesionesActivas = new ConcurrentHashMap<>();

    public void abrirSesion(Long userId, Long puntoId) {
        System.out.println("🔐 [SESIÓN] Punto " + puntoId + " vinculado al usuario " + userId);
        sesionesActivas.put(puntoId, userId);
    }

    public TransaccionEntrega procesarPesoRecibido(Long puntoId, double kilos) {

        if (!sesionesActivas.containsKey(puntoId)) {
            System.err.println("❌ [SESIÓN] Intento de pesaje fallido. Bote " + puntoId + " no tiene sesión abierta.");
            throw new RuntimeException("ERROR: Escanea el QR antes de depositar el plástico.");
        }

        Long usuarioID = sesionesActivas.get(puntoId);
        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto no existe"));
        Long materialId = punto.getMateriales().isEmpty() ? 1L : punto.getMateriales().iterator().next().getId();
        System.out.println("⚖️ [SISTEMA] Usuario " + usuarioID + " entregó " + kilos + "kg en punto " + puntoId);

        TransaccionEntrega resultado = transaccionService.procesarEntrega(usuarioID, puntoId, kilos, null, materialId);

        actualizarNivelFisico(puntoId, kilos);

        sesionesActivas.remove(puntoId);
        System.out.println("✅ [SISTEMA] Sesión finalizada con éxito.");

        return resultado;
    }

    // Método privado para automatizar el estado del bote en el mapa
    private void actualizarNivelFisico(Long puntoId, double kilosNuevos) {
        PuntoRecoleccion punto = puntoRepo.findById(puntoId).orElseThrow();

        // Calculamos cuánto representa ese peso en porcentaje (%)
        double incremento = (kilosNuevos * 100) / punto.getCapacidadMaximakg();
        double nivelFinal = punto.getNivelLlenado() + incremento;

        punto.setNivelLlenado(Math.min(nivelFinal, 100.0)); // No pasar del 100%

        // Si el bote está muy lleno, activar ALERTA para encargados (V2.2 Uber)
        if (punto.getNivelLlenado() >= 85.0) {
            punto.setNecesitaRecoleccion(true);
            punto.setEstadoBote("LLENO/CRÍTICO");
            System.out.println("🚨 [ALERTA] El bote " + punto.getNombre() + " requiere recolección inmediata.");
        } else if (punto.getNivelLlenado() > 40.0) {
            punto.setEstadoBote("NIVEL MEDIO");
        }
        puntoRepo.save(punto);
    }
}
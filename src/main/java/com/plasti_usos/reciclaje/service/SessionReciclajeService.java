package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionReciclajeService {

    @Autowired
    private TransaccionService transaccionService;

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
        System.out.println("⚖️ [SISTEMA] Usuario " + usuarioID + " entregó " + kilos + "kg en punto " + puntoId);

        TransaccionEntrega resultado = transaccionService.procesarEntrega(usuarioID, puntoId, kilos);

        sesionesActivas.remove(puntoId);
        System.out.println("✅ [SISTEMA] Sesión finalizada con éxito.");

        return resultado;
    }
}
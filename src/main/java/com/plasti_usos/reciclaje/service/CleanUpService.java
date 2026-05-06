package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.repository.PinVerificacionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CleanUpService {

    private final PinVerificacionRepository pinRepository;

    public CleanUpService(PinVerificacionRepository pinRepository) {
        this.pinRepository = pinRepository;
    }

    // 🚀 Protocolo de Purga: Se ejecuta cada 10 minutos
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void purgarPinsObsoletos() {
        System.out.println("🧹 [GTI-CLEANUP] Escaneando códigos caducados...");

        // 1. Buscamos y eliminamos códigos expirados o ya usados
        // La lógica del repo debe soportar borrar por fecha anterior a 'ahora'
        LocalDateTime limite = LocalDateTime.now();

        try {
            pinRepository.findAll().stream()
                    .filter(pin -> pin.isUsado() || pin.getFechaExpiracion().isBefore(limite))
                    .forEach(pin -> pinRepository.delete(pin));

            System.out.println("✅ [GTI-CLEANUP] Memoria de verificación optimizada.");
        } catch (Exception e) {
            System.err.println("❌ [GTI-CLEANUP] Error en la purga: " + e.getMessage());
        }
    }
}
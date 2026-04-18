package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.service.SessionReciclajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IotSessionController {

    @Autowired
    private SessionReciclajeService sessionService;

    // --- 1. SCAN QR (Lo llama el Ciudadano desde el celular) ---
    @PostMapping("/abrir-sesion")
    public ResponseEntity<String> abrirSesion(@RequestParam Long userId, @RequestParam Long puntoId) {
        System.out.println("📸 [QR SCAN] Usuario ID: " + userId + " activó el Bote ID: " + puntoId);
        sessionService.abrirSesion(userId, puntoId);
        return ResponseEntity.ok("SESION_ABIERTA_ESPERANDO_PESO");
    }

    // --- 2. RECEPCIÓN DE PESO (Lo llama el Arduino / ESP32) ---
    // El JSON que manda el Arduino: { "puntoId": 4, "kilos": 5.4 }
    @PostMapping("/registrar-peso")
    public ResponseEntity<TransaccionEntrega> registrarPeso(@RequestBody Map<String, Object> data) {
        
        Long puntoId = Long.valueOf(data.get("puntoId").toString());
        double kilos = Double.parseDouble(data.get("kilos").toString());

        System.out.println("⚖️ [HARDWARE] Peso detectado en Punto " + puntoId + " : " + kilos + "kg");
        
        TransaccionEntrega resultado = sessionService.procesarPesoRecibido(puntoId, kilos);
        return ResponseEntity.ok(resultado);
    }
}
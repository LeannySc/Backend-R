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

    /**
     * 📸 SCAN QR INTELIGENTE (Lo llama el Ciudadano o el Encargado desde la
     * Web/Celular)
     * Decide si abrir sesión para pesaje o vaciar el contenedor según el ROL.
     */
    @PostMapping("/escaneo-qr")
    public ResponseEntity<String> gestionarEscaneo(@RequestParam Long userId, @RequestParam Long puntoId) {
        try {
            System.out.println("📸 [EVENTO] Scan recibido de User: " + userId + " para Bote: " + puntoId);
            String resultado = sessionService.procesarEscaneoQR(userId, puntoId);

            // 💡 Importante: Si la sesión ya está ocupada, devolvemos un 409 (Conflict)
            // para que React sepa que no fue un éxito.
            if (resultado.contains("ERROR")) {
                return ResponseEntity.status(409).body(resultado);
            }

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en servicio: " + e.getMessage());
        }
    }

    /**
     * ⚖️ RECEPCIÓN DE PESO (Lo llama el Arduino / Wokwi)
     * JSON esperado: { "puntoId": 9, "kilos": 2.5 }
     */
    @PostMapping("/registrar-peso")
    public ResponseEntity<TransaccionEntrega> registrarPeso(@RequestBody Map<String, Object> data) {
        Long puntoId = Long.valueOf(data.get("puntoId").toString());
        double kilos = Double.parseDouble(data.get("kilos").toString());

        System.out.println("⚖️ [ARDUINO-SIGNAL] Peso en Punto " + puntoId + ": " + kilos + "kg");
        TransaccionEntrega resultado = sessionService.procesarPesoRecibido(puntoId, kilos);
        return ResponseEntity.ok(resultado);
    }
}
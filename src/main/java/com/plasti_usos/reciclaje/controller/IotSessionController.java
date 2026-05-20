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
    public ResponseEntity<?> registrarPeso(@RequestBody Map<String, Object> data) {
        try {

            Long puntoId = Long.valueOf(data.get("puntoId").toString());
            double kilos = Double.parseDouble(data.get("kilos").toString());

            System.out.println("⚖️ [ARDUINO-SIGNAL] Peso en Punto " + puntoId + ": " + kilos + "kg");

            // Procesa normalmente la transacción
            TransaccionEntrega resultadoTransaccion = sessionService.procesarPesoRecibido(puntoId, kilos);

            // 🔥 Datos para actualizar React
            Integer nuevosPuntos = resultadoTransaccion.getReciclador().getSaldoPuntos();

            // Respuesta personalizada
            Map<String, Object> response = new java.util.HashMap<>();

            response.put("idTransaccion",
                    resultadoTransaccion.getId());

            response.put("puntosOtorgados",
                    resultadoTransaccion.getPuntosOtorgados());

            response.put("totalPuntosUsuario",
                    nuevosPuntos);

            response.put("mensaje",
                    "Carga procesada con éxito!");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            System.err.println("⚠️ [BLOQUEO IoT] " + e.getMessage());

            return ResponseEntity
                    .status(409)
                    .body(e.getMessage());

        } catch (Exception e) {

            System.err.println("❌ Error en IotSessionController: " + e.getMessage());

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body("Error interno del servidor al registrar peso.");
        }
    }
}
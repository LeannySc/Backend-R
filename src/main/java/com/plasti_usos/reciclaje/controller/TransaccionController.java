package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.service.TransaccionService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    @Autowired
    private TransaccionService service;

    @PostMapping("/entregar")
    public ResponseEntity<?> realizarEntrega(@RequestParam Long userId, @RequestParam Long puntoId,
            @RequestParam double kilos, @RequestParam(required = false) Long encargadoId,
            @RequestParam Long materialId) {
        try {
            TransaccionEntrega resultado = service.procesarEntrega(userId, puntoId, kilos, encargadoId, materialId);

            // Creamos una respuesta plana para evitar el bucle infinito de Jackson
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Carga Validada con Éxito");
            response.put("puntosGanados", resultado.getPuntosOtorgados());
            response.put("idTransaccion", resultado.getId());
            response.put("usuario", resultado.getReciclador().getNombre());

            if (resultado.getEncargado() != null) {
                response.put("operario", resultado.getEncargado().getNombre());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log para ver el error real en la consola de Spring
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error GTI-3: " + e.getMessage());
        }
    }

    /*
     * @PostMapping("/entregar")
     * public TransaccionEntrega realizarEntrega(@RequestParam Long
     * userId, @RequestParam Long puntoId,
     * 
     * @RequestParam double kilos) {
     * return service.procesarEntrega(userId, puntoId, kilos);
     * }
     */

}
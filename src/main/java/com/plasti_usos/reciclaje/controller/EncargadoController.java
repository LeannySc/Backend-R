package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.service.EncargadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/encargado")
@CrossOrigin(origins = "*")
public class EncargadoController {

    @Autowired
    private EncargadoService encargadoService;

    // Fiel al método del UML: aprobadoEntrega(id):void
    @PostMapping("/aprobar")
    public String aprobar(@RequestParam Long transaccionId, @RequestParam Long encargadoId) {
        encargadoService.aprobarEntrega(transaccionId, encargadoId);
        return "✅ Transacción " + transaccionId + " validada con éxito.";
    }
}
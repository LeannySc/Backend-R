package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.service.EncargadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/encargado")
@CrossOrigin(origins = "*")
public class EncargadoController {

    @Autowired
    private EncargadoService encargadoService;

    @PostMapping("/aprobar")
    public String aprobar(@RequestParam Long transaccionId, @RequestParam Long encargadoId) {
        encargadoService.aprobarEntrega(transaccionId, encargadoId);
        return "✅ Transacción " + transaccionId + " validada con éxito.";
    }

    @PostMapping("/{puntoId}/Vaciar")
    public String vaciarContenedor(@PathVariable Long puntoId, @RequestParam Long encargadoId) {
        encargadoService.vaciarPunto(puntoId, encargadoId);
        return "✅ Punto de recolección " + puntoId + " vaciado con éxito.";
    }

}
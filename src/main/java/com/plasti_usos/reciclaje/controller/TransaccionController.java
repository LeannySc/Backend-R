package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = "*")
public class TransaccionController {

    @Autowired
    private TransaccionService service;

    @PostMapping("/entregar")
    public TransaccionEntrega realizarEntrega(@RequestParam Long userId, @RequestParam Long puntoId,
            @RequestParam double kilos) {
        return service.procesarEntrega(userId, puntoId, kilos);
    }
}
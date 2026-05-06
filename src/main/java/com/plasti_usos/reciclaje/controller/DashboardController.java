package com.plasti_usos.reciclaje.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.plasti_usos.dto.DashboardDTO;
import com.plasti_usos.reciclaje.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    @Autowired
    private DashboardService service;

    @GetMapping("/resumen/{id}")
    public ResponseEntity<DashboardDTO> getResumen(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerDatos(id));
    }
}
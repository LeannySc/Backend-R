package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/puntos")
@CrossOrigin(origins = "*")
public class PuntoController {

    @Autowired
    private PuntoRecoleccionRepository repository;

    @GetMapping("/todos")
    public List<PuntoRecoleccion> listarPuntos() {
        return repository.findAll();
    }

    @PostMapping("/crear")
    public PuntoRecoleccion crear(@RequestBody PuntoRecoleccion punto) {
        return repository.save(punto);
    }
    @GetMapping("/{id}")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    

}
package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.PedidoCanje;
import com.plasti_usos.reciclaje.model.ProductoMaravilla;
import com.plasti_usos.reciclaje.repository.ProductoRepository;
import com.plasti_usos.reciclaje.service.CanjeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/canje")
@CrossOrigin(origins = "*")
public class CanjeController {

    @Autowired
    private CanjeService canjeService;
    @Autowired
    private ProductoRepository productoRepo;

    @GetMapping("/catalogo")
    public List<ProductoMaravilla> verCatalogo() {
        return productoRepo.findAll();
    }

    @PostMapping("/producto/crear")
    public ProductoMaravilla crearProducto(@RequestBody ProductoMaravilla p) {
        return productoRepo.save(p);
    }

    @PostMapping("/realizar")
    public PedidoCanje realizarCanje(@RequestParam Long userId, @RequestParam Long productoId,
            @RequestParam String direccion) {
        return canjeService.procesarCanje(userId, productoId, direccion);
    }
}
package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.PedidoCanje;
import com.plasti_usos.reciclaje.model.ProductoMaravilla;
import com.plasti_usos.reciclaje.repository.ProductoRepository;
import com.plasti_usos.reciclaje.service.CanjeService;
import com.plasti_usos.reciclaje.repository.PedidoCanjeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/canje")
@CrossOrigin(origins = "*")
public class CanjeController {

    @Autowired
    private CanjeService canjeService;
    @Autowired
    private ProductoRepository productoRepo;
    @Autowired
    private PedidoCanjeRepository pedidoRepo;

    @GetMapping("/catalogo")
    public List<ProductoMaravilla> verCatalogo() {
        return productoRepo.findAll();
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<ProductoMaravilla> obtenerProducto(@PathVariable Long id) {
        return productoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @GetMapping("/mis-canjes/{userId}")
    public List<PedidoCanje> verMisCanjes(@PathVariable Long userId) {
        return pedidoRepo.findByRecicladorId(userId);
    }

}
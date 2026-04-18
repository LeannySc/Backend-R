package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.ProductoMaravilla;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private TransaccionEntregaRepository transaccionRepo;
    @Autowired
    private ProductoRepository productoRepo;

    @GetMapping("/reportes")
    public Map<String, Object> verReportes() {
        Map<String, Object> reporte = new HashMap<>();

        reporte.put("totalUsuarios", usuarioRepo.count());
        Double totalkg = transaccionRepo.sumarkilosTotalesValidados();
        reporte.put("stockProductos", productoRepo.findAll());
        reporte.put("totalKilos", totalkg != null ? totalkg : 0.0);
        return reporte;
    }

    @GetMapping("/gestionar-usuarios")
    public List<Usuario> gestionarUsuarios() {
        return usuarioRepo.findAll();
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoMaravilla> crearProducto(@RequestBody ProductoMaravilla nuevo) {
        
        if (nuevo.getNombre() == null || nuevo.getCostoPuntos() < 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(productoRepo.save(nuevo));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoMaravilla> editarProducto(@PathVariable Long id,
            @RequestBody ProductoMaravilla datos) {

        System.out.println("AMIND Actualizando producto ID:" + id);
        return productoRepo.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setDescripcion(datos.getDescripcion());
            p.setCostoPuntos(datos.getCostoPuntos());
            p.setStock(datos.getStock());
            p.setImagenUrl(datos.getImagenUrl());
            p.setActivo(datos.isActivo());
            return ResponseEntity.ok(productoRepo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/productos/todos")
    public List<ProductoMaravilla> listarTodoElCatalogo() {
        return productoRepo.findAll();
    }

}
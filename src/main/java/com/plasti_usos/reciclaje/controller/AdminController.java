package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.EstadoTransaccion;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
        double totalKg = transaccionRepo.findAll().stream().filter(t -> t.getEstado() == EstadoTransaccion.VALIDADA)
                .mapToDouble(t -> t.getCantidadKilos()).sum();

        reporte.put("stockProductos", productoRepo.findAll());
        reporte.put("totalKilos", totalKg);
        return reporte;
    }

    @GetMapping("/gestionar-usuarios")
    public List<Usuario> gestionarUsuarios() {
        return usuarioRepo.findAll();
    }

}
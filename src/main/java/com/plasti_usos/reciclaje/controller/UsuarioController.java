package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identidad")
@CrossOrigin(origins = "*") // Importante: Permite que React se conecte sin bloqueos
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return service.registrarNuevoUsuario(usuario);
    }
}
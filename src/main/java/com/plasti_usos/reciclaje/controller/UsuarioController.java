package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;
import com.plasti_usos.reciclaje.service.UsuarioService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identidad")
@CrossOrigin(origins = "*") // Importante: Permite que React se conecte sin bloqueos
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService service;

    UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario usuario) {
        return service.registrarNuevoUsuario(usuario);
    }

    @PostMapping("/fabricar")
    public Usuario fabricarConFactory(@RequestParam Rol rol, @RequestBody Map<String, Object> datos) {
        return service.fabricarUsuario(rol, datos);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> credenciales) {
        Usuario user = service.login(credenciales.get("correo"), credenciales.get("contrasena"));
        System.out.println("[LOGIN SUCCES]ID cargado: " + user.getId());
        return ResponseEntity.ok(user);

    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Boolean> actualizar(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        System.out.println("[API] Actualizando usuario ID: " + id);
        boolean exito = service.modificarPerfil(id, datos.get("nombre"), datos.get("contrasena"));
        return ResponseEntity.ok(exito);
    }

}
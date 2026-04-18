package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.Reciclador;
import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;
import com.plasti_usos.reciclaje.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public ResponseEntity<Usuario> registrar(@RequestBody Map<String, Object> datos) {
        String rolStr = datos.getOrDefault("rol", "RECICLADOR").toString();
        Rol rol = Rol.valueOf(rolStr);

        Usuario creado = service.fabricarUsuario(rol, datos);
        return ResponseEntity.ok(creado);
    }

    @PostMapping("/fabricar")
    public ResponseEntity<Usuario> fabricarConFactory(@RequestParam Rol rol, @RequestBody Map<String, Object> datos) {
        Usuario creado = service.fabricarUsuario(rol, datos);
        return ResponseEntity.ok(creado);
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

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/historial")
    public List<TransaccionEntrega> verHistorial(@PathVariable Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        System.out.println("[HISTORIAL] Obteniendo historial para usuario ID: " + id + " con rol: " + u.getRol());

        if (u instanceof Reciclador) {
            return ((Reciclador) u).verHistorial();
        }
        return new ArrayList<>();
        // return service.obtenerHistorial(ID);
    }

    @PostMapping("/verificar")
    public ResponseEntity <Boolean> verificarCuenta(@RequestParam String correo, @RequestParam String codigo  ){
        System.out.println("[API] Verificando cuenta para: " + correo);
        boolean resultado = service.validarCuenta(correo, codigo);
        return ResponseEntity.ok(resultado);
        //TODO: process POST request
    }
    

}

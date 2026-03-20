package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.Administrador;
import com.plasti_usos.reciclaje.model.EncargadoPunto;
import com.plasti_usos.reciclaje.model.Reciclador;
import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // IMPLEMENTACIÓN DE LA FABRICA (UML: Factory Usuarios)
    public Usuario fabricarUsuario(Rol tipo, Map<String, Object> datos) {
        Usuario nuevoUsuario;

        if (tipo == Rol.RECICLADOR) {
            System.out.println("[FACTORY] Creando instancia concreta: RECICLADOR");
            System.out.println("[FACTORY] Instanciando producto concreto: RECICLADOR");
            Reciclador r = new Reciclador();
            r.setSaldoPuntos(0);
            nuevoUsuario = r;
        } else if (tipo == Rol.ENCARGADO) {
            System.out.println("[FACTORY] Creando instancia concreta: ENCARGADO_PUNTO");
            System.out.println("[FACTORY] Instanciando Producto Concreto: ENCARGADO_PUNTO");
            EncargadoPunto e = new EncargadoPunto();
            e.setPuntoID(Long.valueOf(datos.get("puntoID").toString()));
            nuevoUsuario = e;
        } else {
            nuevoUsuario = new Administrador();
            System.out.println("[FACTORY] Creando instancia concreta: ADMINISTRADOR");
        }
        nuevoUsuario.setNombre(datos.get("nombre").toString());
        nuevoUsuario.setCorreo(datos.get("correo").toString());
        nuevoUsuario.setContrasena(datos.get("contrasena").toString());
        nuevoUsuario.setRol(tipo);

        return usuarioRepository.save(nuevoUsuario); // Por ahora para validar que el equipo vea la estructura
    }

    public Usuario registrarNuevoUsuario(Usuario datos) {
        // Aquí podríamos agregar lógica adicional como validaciones, hashing de
        // contraseñas, etc.
        if (datos.getRol() == Rol.RECICLADOR) {
            System.out.println("[REGISTRO] Registrando nuevo usuario como RECICLADOR");
            datos.setSaldoPuntos(0); // Inicializamos el saldo de puntos para recicladores
        }
        return usuarioRepository.save(datos);
    }

    public Usuario login(String correo, String contrasena) {
        Usuario user = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getContrasena().equals(contrasena)) {
            System.out.println("[LOGIN] Usuario autenticado: " + correo);
            return user;
        } else {
            System.out.println("[LOGIN] Contraseña incorrecta para: " + correo);
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    @Transactional
    public boolean modificarPerfil(Long id, String nuevoNombre, String nuevaContrasena) {
        return usuarioRepository.findById(id).map(u -> {
            System.out.println("[DATABASE] Guardando cambios para: " + u.getCorreo());
            u.setNombre(nuevoNombre);

            // Solo cambiamos la contraseña si el usuario escribió una nueva
            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                u.setContrasena(nuevaContrasena);
            }

            usuarioRepository.save(u);
            System.out.println("✅ [DATABASE] Datos guardados físicamente en Postgres para ID: " + id);
            return true;
        }).orElse(false);
    }

    // user.setNombre(nuevoNombre);
    // user.setContrasena(nuevaContrasena);
    // usuarioRepository.save(user);
    // System.out.println("[PERFIL] Perfil actualizado para: " + user.getCorreo());
    // return true;
}

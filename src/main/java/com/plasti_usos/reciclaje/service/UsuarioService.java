package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    // IMPLEMENTACIÓN DE LA FABRICA (UML: Factory Usuarios)
    public Usuario fabricarUsuario(Rol tipo, Map<String, Object> datos) {
        // En Java usaremos una instancia anónima o herencia directa
        // Pero lo más limpio es asignar las propiedades según la clase hija lógica del
        // UML

        if (tipo == Rol.RECICLADOR) {
            System.out.println("[FACTORY] Creando instancia concreta: RECICLADOR");
            // Implementaríamos la clase hija Reciclador más adelante si necesitamos métodos
            // específicos
        }

        if (tipo == Rol.ENCARGADO) {
            System.out.println("[FACTORY] Creando instancia concreta: ENCARGADO_PUNTO");
        }

        return null; // Por ahora para validar que el equipo vea la estructura
    }
    public Usuario registrarNuevoUsuario(Usuario datos) {
        // Aquí podríamos agregar lógica adicional como validaciones, hashing de
        // contraseñas, etc.
        if(datos.getRol() == Rol.RECICLADOR) {
            System.out.println("[REGISTRO] Registrando nuevo usuario como RECICLADOR");
            datos.setSaldoPuntos(0); // Inicializamos el saldo de puntos para recicladores
        }
        return usuarioRepository.save(datos);
    }


}
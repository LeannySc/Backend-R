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
            String idString = obtenerDatoSeguro(datos, "puntoID", "0");
            e.setPuntoID(Long.valueOf(idString));
            nuevoUsuario = e;
        } else {
            nuevoUsuario = new Administrador();
            System.out.println("[FACTORY] Creando instancia concreta: ADMINISTRADOR");
        }
        nuevoUsuario.setNombre(obtenerDatoSeguro(datos, "nombre", "Sin Nombre"));
        nuevoUsuario.setCorreo(obtenerDatoSeguro(datos, "correo", "error@plastiusos.com"));
        nuevoUsuario.setContrasena(obtenerDatoSeguro(datos, "contrasena", "1234"));
        nuevoUsuario.setRol(tipo);

        String pin = String.valueOf((int) (Math.random() * 899999) + 100000);
        nuevoUsuario.setCodigoVerificacion(pin);
        System.out.println("[FACTORY] Código de verificación generado: " + pin);
        nuevoUsuario.setVerificado(false);

        String correoInput = obtenerDatoSeguro(datos, "correo", "");
        if (usuarioRepository.findByCorreo(correoInput).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        System.out.print("[EMAIL SIMULATION] Enviando correo de verificación a " + nuevoUsuario.getCorreo()
                + " con el código: " + pin);
        System.out.println();

        return usuarioRepository.save(nuevoUsuario);
    }

    public Usuario registrarNuevoUsuario(Usuario datos) {

        if (datos.getRol() == Rol.RECICLADOR) {
            System.out.println("[REGISTRO] Registrando nuevo usuario como RECICLADOR");
            datos.setSaldoPuntos(0);
        }
        return usuarioRepository.save(datos);
    }

    public Usuario login(String correo, String contrasena) {
        Usuario user = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isVerificado()) {
            throw new RuntimeException("Cuenta no verificada. Por favor, verifica tu correo antes de iniciar sesión.");
        }

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

            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                u.setContrasena(nuevaContrasena);
            }

            usuarioRepository.save(u);
            System.out.println("✅ [DATABASE] Datos guardados físicamente en Postgres para ID: " + id);
            return true;
        }).orElse(false);
    }

    private String obtenerDatoSeguro(Map<String, Object> datos, String llave, String valorDefecto) {
        return (datos.get(llave) != null) ? datos.get(llave).toString() : valorDefecto;
    }

    public boolean validarCuenta(String correo, String codigo) {
        return usuarioRepository.findByCorreo(correo).map(u -> {
            if (u.getCodigoVerificacion().equals(codigo)) {
                u.setVerificado(true);
                usuarioRepository.save(u);
                System.out.println("[VALIDACIÓN] Cuenta verificada para: " + correo);
                return true;
            } else {
                System.out.println("[VALIDACIÓN] Código de verificación incorrecto para: " + correo);
                return false;
            }
        }).orElse(false);
    }
}

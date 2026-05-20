package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.Notificacion;
import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.NotificacionRepository;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository repo;
    @Autowired
    private UsuarioRepository userRepo;

    public void enviar(Usuario u, String titulo, String mensaje, String sub, String origen, boolean email) {
        Notificacion n = new Notificacion();
        n.setUsuario(u);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setSubtexto(sub);
        n.setRolDestino(u.getRol());
        n.setClaseOrigen(origen);
        n.setConEmail(email);
        repo.save(n);
    }

    public void alertarAdministradores(String titulo, String mensaje, String sub, String origen) {
        // 🔍 Buscamos a todos los usuarios con ROL ADMINISTRADOR
        userRepo.findAll().stream()
                .filter(u -> u.getRol() == Rol.ADMINISTRADOR)
                .forEach(admin -> {
                    enviar(admin, titulo, mensaje, sub, origen, true); // true = Importante
                });
    }

}
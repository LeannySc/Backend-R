package com.plasti_usos.reciclaje.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioNotificaciones {

    @Autowired
    private List<NotificadorUsuarios> suscriptores;

    public void suscribir(NotificadorUsuarios obs) {
        suscriptores.add(obs);
    }

    public void descubrir(NotificadorUsuarios obs) {

    }

    public void notificar(String mensaje) {
        System.out.println(" [NOTIFICADOR SERVICE] Despachando a " + suscriptores.size() + " suscriptores...");
        for (NotificadorUsuarios s : suscriptores) {
            s.actualizar(mensaje);

        }

    }
}
package com.plasti_usos.reciclaje.service;

import org.springframework.stereotype.Component;

@Component
public class NotificadorPush implements Notificador {

    @Override
    public void enviar(String m) {
        // Lógica para enviar notificación push
        System.out.println("[PUSH NOTIFICACION]" + m);
    }

}

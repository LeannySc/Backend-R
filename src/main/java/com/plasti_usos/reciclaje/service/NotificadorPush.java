package com.plasti_usos.reciclaje.service;

import org.springframework.stereotype.Component;

@Component
public class NotificadorPush implements NotificadorUsuarios {

    private String tokenDispositivo = "ID-MOVILE-TEST-123";

    @Override
    public void actualizar(String mensaje) {
        System.out.println(" [PUSH NOTIFICATION SENT]: " + mensaje + " a dispositivo " + tokenDispositivo);
    }

}

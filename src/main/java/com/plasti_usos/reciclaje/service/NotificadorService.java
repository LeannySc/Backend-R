package com.plasti_usos.reciclaje.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificadorService {

    @Autowired
    private List<Notificador> notulistas;

    public void notificar(String mensaje) {
        notulistas.forEach(n -> n.enviar(mensaje));
    }

}

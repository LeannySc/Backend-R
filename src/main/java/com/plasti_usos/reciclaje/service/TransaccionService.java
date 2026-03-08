package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.*;
import com.plasti_usos.reciclaje.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionEntregaRepository transaccionRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private PuntoRecoleccionRepository puntoRepo;
    @Autowired
    private CalculadoraPuntos calculadora; // Aquí inyecta tu estrategia (Estándar o Campaña)

    @Transactional
    public TransaccionEntrega procesarEntrega(Long usuarioId, Long puntoId, double kilos) {
        Usuario reciclador = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto no encontrado"));

        // 1. Aplicamos el Patrón Strategy para calcular puntos
        int puntosGañados = calculadora.calcular(kilos);

        // 2. Actualizamos el saldo del usuario (Lógica del UML)
        int saldoActual = reciclador.getSaldoPuntos() != null ? reciclador.getSaldoPuntos() : 0;
        reciclador.setSaldoPuntos(saldoActual + puntosGañados);
        usuarioRepo.save(reciclador);

        // 3. Registramos la transacción
        TransaccionEntrega t = new TransaccionEntrega();
        t.setReciclador(reciclador);
        t.setPunto(punto);
        t.setCantidadKilos(kilos);
        t.setPuntosOtorgados(puntosGañados);

        return transaccionRepo.save(t);
    }
}
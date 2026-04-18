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
    private CalculadoraPuntos calculadora; 
    /*
     * @Autowired
     * private CalculoCampana calculadora; // Inyectamos la estrategia de campaña
     * para futuras pruebas
     */
    @Autowired
    private ServicioNotificaciones notificadorService;

    @Transactional
    public TransaccionEntrega procesarEntrega(Long usuarioId, Long puntoId, double kilos) {

       
        Usuario baseUser = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!(baseUser instanceof Reciclador)) {
            throw new RuntimeException("El usuario debe ser un reciclador para realizar entregas");
        }
        Reciclador reciclador = (Reciclador) baseUser;

        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto no encontrado"));

        TransaccionEntrega t = new TransaccionEntrega();
        t.setReciclador(reciclador);
        t.setPunto(punto);
        t.setCantidadKilos(kilos);
        t.setEstado(EstadoTransaccion.PENDIENTE);
        
        DetalleEntrega detalle = new DetalleEntrega();
        detalle.setCantidad((float) kilos);
        t.getDetalles().add(detalle);

        int puntosGanados = calculadora.calcular(t.getDetalles());
        t.setPuntosOtorgados(puntosGanados);
        detalle.setPuntosOtorgados(puntosGanados);
        TransaccionEntrega guardada = transaccionRepo.save(t);

        String mensaje = String.format("¡Hola %s! Se ha registrado tu entrega de %.2f kg en %s. Puntos pendientes: %d",
                reciclador.getNombre(), kilos, punto.getNombre(), puntosGanados);

        notificadorService.notificar(mensaje);
        notificadorService.notificar("¡Genial! Has entregado " + kilos + "kg de plástico en Campanario.");

        return guardada;
    }
}
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
    private CalculadoraPuntos calculadora; // Aquí inyecta parte de la estrategia (Estandar o Campaña)
    /*
     * @Autowired
     * private CalculoCampana calculadora; // Inyectamos la estrategia de campaña
     * para futuras pruebas
     */
    @Autowired
    private ServicioNotificaciones notificadorService;

    @Transactional
    public TransaccionEntrega procesarEntrega(Long usuarioId, Long puntoId, double kilos) {

        // 1. Búsqueda de actores (Factory check)
        Usuario baseUser = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!(baseUser instanceof Reciclador)) {
            throw new RuntimeException("El usuario debe ser un reciclador para realizar entregas");
        }
        Reciclador reciclador = (Reciclador) baseUser;

        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto no encontrado"));

        // 2. Creamos la Transacción (Cabecera del UML)
        TransaccionEntrega t = new TransaccionEntrega();
        t.setReciclador(reciclador);
        t.setPunto(punto);
        t.setCantidadKilos(kilos);
        // t.setPuntosOtorgados(puntosGanados);
        t.setEstado(EstadoTransaccion.PENDIENTE);
        // TransaccionEntrega guardada = transaccionRepo.save(t);

        // 3. IMPLEMENTAMOS LA COMPOSICIÓN (Diamante Negro del UML)
        DetalleEntrega detalle = new DetalleEntrega();
        detalle.setCantidad((float) kilos);
        // detalle.setMaterial("Plástico"); // Aquí podrías mejorar para aceptar
        // diferentes materiales
        // detalle.setPuntosOtorgados(puntosGanados);
        // detalle.setMaterial(punto.getMateriales().get(0));
        // t.getDetalles().add(detalle);
        t.getDetalles().add(detalle);// Agregamos a la lista +List<DetalleEntrega>

        // 4. APLICAMOS EL PATRÓN STRATEGY (Firma corregida: List en lugar de double)
        int puntosGanados = calculadora.calcular(t.getDetalles());
        t.setPuntosOtorgados(puntosGanados);
        detalle.setPuntosOtorgados(puntosGanados);

        // 5. Actualizamos saldo en DB de forma segura
        //int saldoActual = reciclador.getSaldoPuntos() != null ? reciclador.getSaldoPuntos() : 0;
        //reciclador.setSaldoPuntos(saldoActual + puntosGanados);
        //usuarioRepo.save(reciclador);
        TransaccionEntrega guardada = transaccionRepo.save(t);

        // 6. El TRIGGER del Patrón Observer
        String mensaje = String.format("¡Hola %s! Se ha registrado tu entrega de %.2f kg en %s. Puntos pendientes: %d",
                reciclador.getNombre(), kilos, punto.getNombre(), puntosGanados);

        notificadorService.notificar(mensaje);
        notificadorService.notificar("¡Genial! Has entregado " + kilos + "kg de plástico en Campanario.");

        return guardada;
    }
}
package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.*;
import com.plasti_usos.reciclaje.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.*;

@Service
public class SessionReciclajeService {

    @Autowired
    private TransaccionService transaccionService;
    @Autowired
    private PuntoRecoleccionRepository puntoRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private EncargadoService encargadoService;
    @Autowired
    private NotificacionService notificador;

    // RAM GTI: Almacena BoteID -> UserID
    private ConcurrentHashMap<Long, Long> sesionesActivas = new ConcurrentHashMap<>();
    // Reloj GTI: Controla el cierre automático por inactividad
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ConcurrentHashMap<Long, ScheduledFuture<?>> tareasCierre = new ConcurrentHashMap<>();

    @Transactional
    public String procesarEscaneoQR(Long userId, Long puntoId) {
        Usuario u = usuarioRepo.findById(userId).orElseThrow();
        PuntoRecoleccion p = puntoRepo.findById(puntoId).orElseThrow();

        if (u.getRol() == Rol.ENCARGADO || u.getRol() == Rol.ADMINISTRADOR) {
            encargadoService.vaciarPunto(puntoId, userId);
            return "ACCION_VACIADO_COMPLETA";
        }

        if (p.isOcupado() && !userId.equals(sesionesActivas.get(puntoId))) {
            return "ERROR_BOTE_OCUPADO_POR_OTRO_USUARIO";
        }

        // ✅ ABRIMOS SESIÓN
        sesionesActivas.put(puntoId, userId);
        p.setOcupado(true);
        puntoRepo.save(p);

        // ⏱️ PROTOCOLO 20 SEGUNDOS: Si el usuario no deposita nada, cerramos por
        // timeout
        programarAutoCierre(puntoId, 300);

        System.out.println("⏱️ [TIMEOUT START] Bote " + puntoId + " esperando peso por 5 min...");
        return "SESION_CIUDADANO_ABIERTA";
    }

    @Transactional
    public TransaccionEntrega procesarPesoRecibido(Long puntoId, double kilos) {
        if (!sesionesActivas.containsKey(puntoId)) {
            throw new RuntimeException("ERROR: Escanea el QR antes de depositar.");
        }

        // Cancelamos el cierre de los 20 segundos porque el peso YA llegó
        cancelarTimer(puntoId);

        Long usuarioID = sesionesActivas.get(puntoId);
        PuntoRecoleccion punto = puntoRepo.findById(puntoId).orElseThrow();

        double nivelAntes = punto.getNivelLlenado();

        Long materialId = punto.getMateriales().isEmpty() ? 1L : punto.getMateriales().iterator().next().getId();

        // 💰 Ejecutamos pago de puntos
        TransaccionEntrega resultado = transaccionService.procesarEntrega(usuarioID, puntoId, kilos, null, materialId);

        // 📊 Llenamos el bote en el mapa
        punto.registrarNuevoPeso(kilos);
        if (punto.getNivelLlenado() >= 85.0 && nivelAntes < 85.0) {
            notificador.alertarAdministradores(
                    "ESTADO CRÍTICO: " + punto.getNombre(),
                    "El sensor IoT reporta capacidad al " + String.format("%.1f", punto.getNivelLlenado()) + "%.",
                    "Ubicación: " + punto.getDireccion(),
                    "Hardware Logic: " + puntoId);
        }
        punto.setOcupado(false); // ✅ Libera el bote
        puntoRepo.save(punto);

        sesionesActivas.remove(puntoId);
        System.out.println("✅ [SINC] Puntos pagados y Bote #" + puntoId + " liberado.");
        return resultado;
    }

    private void programarAutoCierre(Long puntoId, int segundos) {
        cancelarTimer(puntoId);
        ScheduledFuture<?> task = scheduler.schedule(() -> {
            sesionesActivas.remove(puntoId);
            PuntoRecoleccion p = puntoRepo.findById(puntoId).orElse(null);
            if (p != null) {
                p.setOcupado(false);
                puntoRepo.save(p);
                System.out.println("⏳ [AUTO-CLOSE] Bote #" + puntoId + " liberado por inactividad.");
            }
        }, segundos, TimeUnit.SECONDS);
        tareasCierre.put(puntoId, task);
    }

    private void cancelarTimer(Long puntoId) {
        if (tareasCierre.containsKey(puntoId)) {
            tareasCierre.get(puntoId).cancel(false);
            tareasCierre.remove(puntoId);
        }
    }

}
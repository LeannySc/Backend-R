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
    private CalculadoraPuntos calculadora; // 🎯 Estrategia dinámica
    @Autowired
    private TipoMaterialRepository materialRepo;
    @Autowired
    private ServicioNotificaciones notificadorService;
    @Autowired
    private NotificacionService notificador;

    @Transactional
    public TransaccionEntrega procesarEntrega(Long usuarioId, Long puntoId, double kilos, Long encargadoId,
            Long materialId) {

        // 1. VALIDACIONES INICIALES
        Usuario baseUser = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!(baseUser instanceof Reciclador)) {
            throw new RuntimeException("Solo los recicladores pueden recibir puntos.");
        }
        Reciclador reciclador = (Reciclador) baseUser;

        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Estación GTI no encontrada."));

        TipoMaterial material = materialRepo.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material no catalogado."));

        // Asegurar Billetera
        if (reciclador.getBilletera() == null) {
            BilleteraReciclador nb = new BilleteraReciclador();
            nb.setReciclador(reciclador);
            reciclador.setBilletera(nb);
        }

        // 2. CREACIÓN DE ENTIDADES
        TransaccionEntrega t = new TransaccionEntrega();
        t.setReciclador(reciclador);
        t.setPunto(punto);
        t.setCantidadKilos(kilos);
        t.setEstado(EstadoTransaccion.VALIDADA);

        // Registrar auditoría si hay encargado
        if (encargadoId != null) {
            Usuario encargadoObj = usuarioRepo.findById(encargadoId).orElse(null);
            if (encargadoObj instanceof EncargadoPunto e) {
                t.setEncargado(e);
                e.setBotesVaciosTotales(e.getBotesVaciosTotales() + 1);
            }
        }

        // 3. CONSTRUCCIÓN DEL DETALLE (Material seleccionado)
        DetalleEntrega detalle = new DetalleEntrega();
        detalle.setCantidad((float) kilos);
        detalle.setMaterial(material); // ✅ Material enviado desde el selector del front
        t.getDetalles().add(detalle);

        // 🎯 4. CÁLCULO MEDIANTE CALCULADORA (Delegación)
        // La calculadora estándar multiplicará kilos * puntos del material.
        int puntosFinales = calculadora.calcular(t.getDetalles());

        t.setPuntosOtorgados(puntosFinales);
        detalle.setPuntosOtorgados(puntosFinales);

        // 5. ACTUALIZACIÓN DE SALDOS
        int saldoAnt = (reciclador.getSaldoPuntos() != null) ? reciclador.getSaldoPuntos() : 0;
        reciclador.setSaldoPuntos(saldoAnt + puntosFinales);

        double kgAnt = (reciclador.getBilletera().getKilosAportados() != null)
                ? reciclador.getBilletera().getKilosAportados()
                : 0.0;
        reciclador.getBilletera().setKilosAportados(kgAnt + kilos);

        // 6. PERSISTENCIA
        usuarioRepo.save(reciclador);
        usuarioRepo.saveAndFlush(reciclador);
        // TransaccionEntrega guardada = transaccionRepo.save(t);

        // Notificaciones
        String msj = String.format("¡Hola %s! Ganaste %d pts por traer %s.", reciclador.getNombre(), puntosFinales,
                material.getNombre());
        notificadorService.notificar(msj);

        // Justo antes del return
        notificador.enviar(reciclador,
                "Recibiste " + puntosFinales + " puntos GTI",
                "Tu entrega fue validada con éxito",
                "Saldo: " + reciclador.getSaldoPuntos() + " pts",
                "TransaccionService.java",
                false);

        return transaccionRepo.save(t);
        // return guardada;
    }
}
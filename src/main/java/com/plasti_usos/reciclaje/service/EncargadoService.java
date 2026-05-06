package com.plasti_usos.reciclaje.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plasti_usos.reciclaje.model.EncargadoPunto;
//import com.plasti_usos.reciclaje.model.EstadoTransaccion;
import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.model.Usuario;
//import com.plasti_usos.reciclaje.model.Reciclador;
//import com.plasti_usos.reciclaje.model.TransaccionEntrega;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;
//import com.plasti_usos.reciclaje.repository.TransaccionEntregaRepository;
//import com.plasti_usos.reciclaje.repository.UsuarioRepository;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class EncargadoService {

    // @Autowired
    // private TransaccionEntregaRepository transaccionRepo;
    // @Autowired
    // private UsuarioRepository usuarioRepo;
    @Autowired
    private PuntoRecoleccionRepository puntoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    /*
     * @Transactional
     * public void aprobarEntrega(Long transaccionId, Long encargadoId) {
     * TransaccionEntrega t = transaccionRepo.findById(transaccionId)
     * .orElseThrow(() -> new RuntimeException("Error Crítico: Transacción " +
     * transaccionId + " no existe."));
     * 
     * if (t.getEstado() != EstadoTransaccion.PENDIENTE) {
     * throw new
     * RuntimeException("Solo se pueden aprobar transacciones pendientes");
     * }
     * t.setEstado(EstadoTransaccion.VALIDADA);
     * 
     * Reciclador r = (Reciclador) usuarioRepo.findById(t.getReciclador().getId())
     * .orElseThrow(
     * () -> new
     * RuntimeException("Error Crítico: Reciclador no encontrado para la transacción."
     * ));
     * 
     * int puntosNuevos = t.getPuntosOtorgados();
     * int saldoAnterior = (r.getSaldoPuntos() != null) ? r.getSaldoPuntos() : 0;
     * 
     * r.setSaldoPuntos(saldoAnterior + puntosNuevos);
     * t.setEstado(EstadoTransaccion.VALIDADA);
     * 
     * usuarioRepo.save(r);
     * transaccionRepo.save(t);
     * 
     * System.out.println("🚀 [SEGURIDAD] Saldo actualizado con éxito.");
     * 
     * System.out.println("✅ [SECURITY] Billetera Actualizada: " + r.getNombre() +
     * " ahora tiene " + r.getSaldoPuntos()
     * + " pts.");
     * 
     * System.out.println("[ENCARGADO] Transacción ID " + transaccionId +
     * " aprobada por Encargado ID " + encargadoId);
     * 
     * }
     */
    @Transactional
    public void vaciarPunto(Long puntoId, Long encargadoId) {

        PuntoRecoleccion punto = puntoRepo.findById(puntoId)
                .orElseThrow(() -> new RuntimeException("Punto de recolección no encontrado"));
        punto.setNivelLlenado(0.0);
        punto.setEstadoBote("VACÍO");
        punto.reportarVaciado();
        puntoRepo.save(punto);

        // 2. ✅ CRÍTICO: Premiar al encargado (Tarjeta Azul)
        Usuario encargadoBase = usuarioRepo.findById(encargadoId).orElse(null);
        if (encargadoBase instanceof EncargadoPunto e) {
            e.setBotesVaciosTotales(e.getBotesVaciosTotales() + 1);
            e.setUltimoVaciado(java.time.LocalDateTime.now());
            usuarioRepo.save(e);
        }

        System.out.println("[ENCARGADO] Punto ID " + puntoId + " vaciado por Encargado ID " + encargadoId);
    }
}

package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.*;
import com.plasti_usos.reciclaje.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CanjeService {

    @Autowired
    private ProductoRepository productoRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private PedidoCanjeRepository pedidoRepo;
    @Autowired
    private ServicioNotificaciones notificadorService;
    @Autowired
    private GestorInventario gestorInventario;

    @Transactional
    public PedidoCanje procesarCanje(Long userId, Long productoId, String direccion) {

        // 1. Buscamos el usuario genérico (UML: Clase Padre Usuario)
        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. VERIFICACIÓN DE CLASE ( Factory Pattern Logic )
        if (!(usuario instanceof Reciclador)) {
            throw new RuntimeException("Error: Este usuario no es de tipo Reciclador y no tiene billetera.");
        }

        // 3. CASTING CORRECTO (Convertimos al hijo Reciclador para ver sus puntos)
        Reciclador reciclador = (Reciclador) usuario;

        ProductoMaravilla producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto maravilla no encontrado en Popayán"));

        // 4. PATRÓN SINGLETON (Delegamos la reserva al GestorInventario)
        if (!gestorInventario.reservarStock(productoId)) {
            throw new RuntimeException("¡Maravilla agotada! Alguien más se llevó el último ejemplar.");
        }

        // 5. VALIDACIÓN DE PUNTOS (Fiel a la lógica de negocio)
        int saldoActual = (reciclador.getSaldoPuntos() != null) ? reciclador.getSaldoPuntos() : 0;

        if (saldoActual < producto.getCostoPuntos()) {
            // Regla Pro: Si no tiene puntos, devolvemos el stock al Singleton antes de
            // fallar
            gestorInventario.liberarStock(productoId);
            throw new RuntimeException("Puntos insuficientes para esta maravilla.");
        }

        // 6. EJECUCIÓN (Consistencia de Datos)
        reciclador.setSaldoPuntos(saldoActual - producto.getCostoPuntos());
        usuarioRepo.save(reciclador); // Persistencia

        // 7. CREACIÓN DEL PEDIDO (Módulo Canje Púrpura del UML)
        PedidoCanje pedido = new PedidoCanje();
        pedido.setReciclador(reciclador);
        pedido.setProducto(producto);
        pedido.setDireccionEnvio(direccion);
        PedidoCanje guardado = pedidoRepo.save(pedido);

        // 8. PATRÓN OBSERVER: El Trigger de notificaciones
        String mensaje = String.format("🎁 ¡Hola %s! Canje exitoso por: %s. Descontamos %d pts.",
                reciclador.getNombre(), producto.getNombre(), producto.getCostoPuntos());

        notificadorService.notificar(mensaje);

        return guardado;
    }
}
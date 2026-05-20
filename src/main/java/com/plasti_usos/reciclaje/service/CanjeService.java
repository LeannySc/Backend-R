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

    @Autowired
    private NotificacionService notificador;

    @Transactional
    public PedidoCanje procesarCanje(Long userId, Long productoId, String direccion) {

        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("LOG GTI: Intento de canje por usuario ID: " + userId + " - Rol: " + usuario.getRol());

        if (!(usuario instanceof Reciclador)) {
            throw new RuntimeException("Error: Este usuario no es de tipo Reciclador y no tiene billetera.");
        }

        Reciclador reciclador = (Reciclador) usuario;

        ProductoMaravilla producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto maravilla no encontrado en Popayán"));

        if (!gestorInventario.reservarStock(productoId)) {
            throw new RuntimeException("¡Maravilla agotada! Alguien más se llevó el último ejemplar.");
        }

        int saldoActual = (reciclador.getSaldoPuntos() != null) ? reciclador.getSaldoPuntos() : 0;

        if (saldoActual < producto.getCostoPuntos()) {
            gestorInventario.liberarStock(productoId);
            throw new RuntimeException("Puntos insuficientes para esta maravilla.");
        }
        if (!gestorInventario.reservarStock(productoId)) {
            // 🚨 TRIGGER ADMIN: Agotamiento de recurso
            notificador.alertarAdministradores(
                    "STOCK AGOTADO: " + producto.getNombre(),
                    "Un usuario intentó realizar un canje pero el stock llegó a cero.",
                    "Acción: Reposición inmediata requerida",
                    "CanjeService.java");
            throw new RuntimeException("¡Maravilla agotada! ...");
        }

        reciclador.setSaldoPuntos(saldoActual - producto.getCostoPuntos());
        usuarioRepo.save(reciclador);

        PedidoCanje pedido = new PedidoCanje();
        pedido.setReciclador(reciclador);
        pedido.setProducto(producto);
        pedido.setDireccionEnvio(direccion);
        PedidoCanje guardado = pedidoRepo.save(pedido);

        String mensaje = String.format("🎁 ¡Hola %s! Canje exitoso por: %s. Descontamos %d pts.",
                reciclador.getNombre(), producto.getNombre(), producto.getCostoPuntos());

        notificador.enviar(
                usuario,
                "Canje confirmado — " + producto.getNombre(),
                "Descontamos " + producto.getCostoPuntos() + " pts de tu billetera.",
                "Pedido #" + guardado.getId() + " registrado con éxito", // Subtexto dinámico
                "CanjeService.java", // Origen de la auditoría
                false // Email no enviado por ahora
        );

        notificadorService.notificar(mensaje);

        return guardado;
    }
}
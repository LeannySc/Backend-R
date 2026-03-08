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
    private NotificadorService notificadorService;

    @Transactional
    public PedidoCanje procesarCanje(Long userId, Long productoId, String direccion) {
        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProductoMaravilla producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 1. Validar Stock (Lógica del Gestor de Inventario Singleton)
        if (producto.getStock() <= 0) {
            throw new RuntimeException("Lo sentimos, no hay stock de este producto maravilla");
        }

        // 2. Validar Saldo de Puntos
        if (usuario.getSaldoPuntos() < producto.getCostoPuntos()) {
            throw new RuntimeException("No tienes suficientes puntos para esta maravilla");
        }

        // 3. Ejecutar Canje (Atomicidad)
        usuario.setSaldoPuntos(usuario.getSaldoPuntos() - producto.getCostoPuntos());
        producto.setStock(producto.getStock() - 1);

        usuarioRepo.save(usuario);
        productoRepo.save(producto);

        PedidoCanje pedido = new PedidoCanje();
        pedido.setReciclador(usuario);
        pedido.setProducto(producto);
        pedido.setDireccionEnvio(direccion);

        String mensaje = String.format("Nuevo canje realizado: Usuario %s ha canjeado %s. Dirección de envío: %s",
                usuario.getNombre(), producto.getNombre(), direccion);

        notificadorService.notificar(mensaje);

        return pedidoRepo.save(pedido);

    }
}
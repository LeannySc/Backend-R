package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.ProductoMaravilla;
import com.plasti_usos.reciclaje.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestorInventario {

    @Autowired
    private ProductoRepository productoRepo;

    @Transactional
    public boolean reservarStock(Long productoId) {
        ProductoMaravilla producto = productoRepo.findById(productoId).orElse(null);

        if (producto != null && producto.getDisponibilidad()) {
            producto.setStock(producto.getStock() - 1);
            productoRepo.save(producto);
            System.out.println("✅ [SINGLETON] Stock reservado para producto ID: " + productoId);
            return true;
        }
        return false;
    }

    @Transactional
    public void liberarStock(Long productoId) {
        ProductoMaravilla producto = productoRepo.findById(productoId).orElse(null);
        if (producto != null) {
            producto.setStock(producto.getStock() + 1);
            productoRepo.save(producto);
        }
    }
}
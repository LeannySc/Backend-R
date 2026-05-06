package com.plasti_usos.reciclaje.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;
import com.plasti_usos.reciclaje.service.LogisticaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/logistica")
@CrossOrigin(origins = "*")
public class LogisticaController {

    @Autowired
    private LogisticaService logisticaService;

    @Autowired
    private PuntoRecoleccionRepository puntoRepo;

    // 🚩 EL ENCARGADO BUSCA BOTES LLENOS CERCA
    @GetMapping("/botes-cercanos")
    public List<PuntoRecoleccion> listarBotesLlenosCerca(@RequestParam Double lat, @RequestParam Double lon) {
        return puntoRepo.findAll().stream()
                .filter(p -> p.isNecesitaRecoleccion() && !p.isOcupado())
                .filter(p -> logisticaService.calcularDistancia(lat, lon, p.getLatitud(), p.getLongitud()) <= 5.0)
                .toList();
    }

    // 🚩 EL ENCARGADO ACEPTA LA MISIÓN (Bloqueo Estilo Uber)
    @PostMapping("/aceptar-mision")
    public ResponseEntity<String> aceptarMision(@RequestParam Long puntoId, @RequestParam Long encargadoId) {
        PuntoRecoleccion punto = puntoRepo.findById(puntoId).orElseThrow();

        if (punto.isOcupado()) {
            return ResponseEntity.badRequest().body("❌ Misión fallida: Otro encargado ya va en camino.");
        }

        punto.setOcupado(true);
        punto.setEncargadoEnCaminoId(encargadoId);
        puntoRepo.save(punto);

        return ResponseEntity.ok("✅ Misión aceptada. El bote ha sido reservado para tu recolección.");
    }

    // 🚩 EL ENCARGADO TERMINA LA RECOLECCIÓN (Botón verde)
    @PostMapping("/completar-recoleccion")
    public ResponseEntity<String> completarRecoleccion(@RequestParam Long puntoId) {
        PuntoRecoleccion punto = puntoRepo.findById(puntoId).orElseThrow();

        // Ejecutamos el método que creamos en la Entidad
        punto.reportarVaciado();
        puntoRepo.save(punto);

        return ResponseEntity.ok("✅ Punto vaciado. Ahora está disponible en VERDE para los ciudadanos.");
    }
}
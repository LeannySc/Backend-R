package com.plasti_usos.reciclaje.controller;

import com.plasti_usos.reciclaje.model.PuntoRecoleccion;
import com.plasti_usos.reciclaje.model.TipoMaterial;
import com.plasti_usos.reciclaje.repository.PuntoRecoleccionRepository;
import com.plasti_usos.reciclaje.repository.TipoMaterialRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/puntos")
@CrossOrigin(origins = "*")
public class PuntoController {

    @Autowired
    private PuntoRecoleccionRepository repository;

    @Autowired
    private TipoMaterialRepository materialRepo;

    @GetMapping("/todos")
    public List<PuntoRecoleccion> listarPuntos() {
        return repository.findAll().stream()
                .filter(p -> !"VIRTUAL".equals(p.getEstadoBote()))
                .toList();
    }

    @PostMapping("/crear")
    @Transactional
    public PuntoRecoleccion crear(@RequestBody Map<String, Object> data) {
        PuntoRecoleccion p;

        // ✅ MEJORA 1: Evitar duplicados (Update if ID exists)
        if (data.get("id") != null) {
            Long id = Long.valueOf(data.get("id").toString());
            p = repository.findById(id).orElse(new PuntoRecoleccion());
        } else {
            p = new PuntoRecoleccion();
            p.setNivelLlenado(0.0);
            p.setEstadoBote("VACÍO");
        }

        p.setNombre(data.get("nombre").toString());
        p.setDireccion(data.get("direccion").toString());
        p.setLatitud(Double.valueOf(data.get("latitud").toString()));
        p.setLongitud(Double.valueOf(data.get("longitud").toString()));
        p.setCodigoQR(data.get("codigoQR").toString());

        Object cap = data.get("capacidadMaximakg");
        p.setCapacidadMaximakg(cap != null ? Double.valueOf(cap.toString()) : 50.0);

        // Controlamos el estado activo si viene del front
        if (data.containsKey("activo")) {
            p.setActivo(Boolean.parseBoolean(data.get("activo").toString()));
        }

        // Vínculo seguro de materiales (Lógica ManyToMany corregida)
        Object idsObj = data.get("materialesIds");
        if (idsObj instanceof List<?> rawIds) {
            p.getMateriales().clear();
            rawIds.forEach(obj -> {
                Long matId = Long.valueOf(obj.toString());
                TipoMaterial material = materialRepo.findById(matId).orElse(null);
                if (material != null) {
                    p.getMateriales().add(material);
                }
            });
        }

        return repository.save(p);
    }

    @GetMapping("/{id}")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/monitor-recoleccion")
    public List<PuntoRecoleccion> verBotesLlenos() {
        return repository.findAll().stream()
                .filter(p -> "LLENO".equals(p.getEstadoBote()) || p.isNecesitaRecoleccion())
                .toList();
    }

    @DeleteMapping("/{id}")
    @Transactional // ✅ Esencial para ManyToMany
    public ResponseEntity<String> eliminarPuntoReal(@PathVariable Long id) {
        return repository.findById(id).map(punto -> {
            // 1. Desvinculamos los materiales en la tabla intermedia antes de borrar
            punto.getMateriales().clear();
            repository.save(punto); // Sincroniza la desconexión

            // 2. Ejecutamos el borrado físico de la fila en la base de datos
            repository.delete(punto);

            System.out.println("🗑️ Nodo #" + id + " borrado definitivamente de la base de datos.");
            return ResponseEntity.ok("BORRADO_PERMANENTE_EXITOSO");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 📡 1. Traer botes desconectados (activo = false)
    @GetMapping("/retirados")
    public List<PuntoRecoleccion> listarRetirados() {
        return repository.findAll().stream()
                .filter(p -> !p.isActivo())
                .toList();
    }

    // 🚜 2. Reinstalar Nodo (Para que el Admin lo cambie de lugar y lo active)
    @PutMapping("/reinstalar/{id}")
    @Transactional
    public ResponseEntity<PuntoRecoleccion> reinstalar(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return repository.findById(id).map(p -> {
            // Permitimos cambiar nombre y zona (donde se mueve el bote)
            p.setNombre(data.get("nombre").toString());
            p.setDireccion(data.get("direccion").toString());
            p.setLatitud(Double.valueOf(data.get("latitud").toString()));
            p.setLongitud(Double.valueOf(data.get("longitud").toString()));

            // El código QR y Capacidad se quedan intactos (porque es el mismo hardware)
            p.setActivo(true);
            p.setNivelLlenado(0.0);
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

}
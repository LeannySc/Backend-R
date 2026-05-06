package com.plasti_usos.reciclaje.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.plasti_usos.dto.DashboardDTO;
import com.plasti_usos.dto.DashboardDTO.TransaccionBreve;
import com.plasti_usos.reciclaje.model.*;
import com.plasti_usos.reciclaje.repository.PedidoCanjeRepository;
import com.plasti_usos.reciclaje.repository.TransaccionEntregaRepository;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

@Service
public class DashboardService {
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PedidoCanjeRepository pedidoRepo;

    @Autowired
    private TransaccionEntregaRepository transaccionRepo;

    public DashboardDTO obtenerDatos(Long userId) {
        Usuario u = usuarioRepo.findById(userId).orElseThrow();
        DashboardDTO dto = new DashboardDTO();

        if (u instanceof Reciclador r) {
            dto.setMensajeBienvenida("¡HOLA, " + u.getNombre().toUpperCase() + "!");
            dto.setDescripcionBienvenida("Tu esfuerzo está salvando a Popayán.");

            // 1. PUNTOS REALES (Billetera)
            int saldo = (r.getBilletera() != null) ? r.getBilletera().getSaldoPuntos() : 0;
            dto.setPuntos(saldo + " pts");

            // 2. KILOS TOTALES (Suma del historial)
            double totalKg = (r.getBilletera() != null && r.getBilletera().getKilosAportados() != null)
                    ? r.getBilletera().getKilosAportados()
                    : 0.0;
            dto.setKgReciclados(totalKg + " kg");

            long compras = pedidoRepo.countByRecicladorId(userId); // devuelve 8
            long total = compras;

            dto.setEntregas(total + " Actividades");

            // 📊 PROCESAMIENTO DE GRÁFICA (IDEA 1)
            List<Object[]> resultados = transaccionRepo.obtenerKgPorDia(userId);
            List<Map<String, Object>> graphData = new ArrayList<>();
            List<TransaccionBreve> listaBreve = new ArrayList<>();

            transaccionRepo.findTop4ByRecicladorIdOrderByIdDesc(userId).forEach(t -> {
                TransaccionBreve tx = new TransaccionBreve();
                tx.setLabel(t.getCantidadKilos() + "kg de " +
                        (t.getDetalles().isEmpty() ? "Residuos" : t.getDetalles().get(0).getMaterial().getNombre()));
                tx.setFecha("Hoy"); // O formatear t.getFechaEntrega()
                tx.setPuntos("+" + t.getPuntosOtorgados());
                tx.setTipo("ENTREGA");
                listaBreve.add(tx);
            });

            for (Object[] fila : resultados) {
                Map<String, Object> dataPunto = new HashMap<>();
                dataPunto.put("name", fila[0]); // El día (ej: "05-May")
                dataPunto.put("kg", fila[1]); // La suma (ej: 100.0)
                graphData.add(dataPunto);
            }
            List<Object[]> resultadosMix = transaccionRepo.obtenerDistribucionPorMaterial(userId);
            List<Map<String, Object>> mixData = new ArrayList<>();

            for (Object[] fila : resultadosMix) {
                Map<String, Object> materialData = new HashMap<>();
                materialData.put("name", fila[0]); // Ejemplo: "VIDRIO"
                materialData.put("value", fila[1]); // Ejemplo: 15.0 (kilos)
                mixData.add(materialData);
            }
            dto.setDistribucionMateriales(mixData);
            dto.setHistoricoMensual(graphData);
            dto.setUltimasActividades(listaBreve);

            // 3. REGISTROS (Conteo de transacciones)
            // int conteo = (r.getHistorialEntrega() != null) ?
            // r.getHistorialEntrega().size() : 0;
            // dto.setEntregas(conteo + " Actividades");

            // 4. STATUS RED (Lógica de niveles sugerida)
            String nivel;
            int progreso;
            if (totalKg >= 500) {
                nivel = "LEYENDA VERDE";
                progreso = 100;
            } else if (totalKg >= 100) {
                nivel = "GUERRERO GTI";
                progreso = (int) ((totalKg / 500) * 100);
            } else {
                nivel = "NOVATO ECO";
                progreso = (int) totalKg;
            }

            dto.setCanjes(nivel); // Para la tarjeta 4
            dto.setNombreNivel(nivel);
            dto.setProgresoPorcentaje(progreso);

        } else if (u instanceof EncargadoPunto e) {
            // ... (Mantenemos la lógica de encargado para terminales móviles)
            dto.setMensajeBienvenida("CENTRO DE CONTROL");
            dto.setDescripcionBienvenida("Tu gestión logística mantiene a Popayán limpia.");
            // dto.setPuntos("$ " + (e.getBotesVaciosTotales() * 5000));
            // dto.setKgReciclados(e.getBotesVaciosTotales() + " Recogidos");
            // dto.setEntregas("Online");
            // dto.setCanjes("Operario ACTIVO");

            // 🟢 1. CARGA GESTIONADA (Sumamos kg de sus tickets manuales)
            Double kgTickets = transaccionRepo.sumarKgProcesadosPorEncargado(e.getId());
            double totalKgGestionados = (kgTickets != null ? kgTickets : 0.0);
            dto.setPuntos(totalKgGestionados + " KG"); // stat1 (Cuadro Verde)

            // 🔵 2. LOGÍSTICA DE NODOS (Botes vaciados por misiones GPS)
            int vaciados = (e.getBotesVaciosTotales() != null) ? e.getBotesVaciosTotales() : 0;
            dto.setKgReciclados(vaciados + " Nodos Vaciados"); // stat2 (Cuadro Azul)

            // 🟡 3. FLUJO DE CIUDADANOS (Conteo de tickets de terminal humana)
            long txManuales = transaccionRepo.countByEncargadoId(e.getId());
            dto.setEntregas(txManuales + " Tickets Generados"); // stat3 (Cuadro Amarillo)

            // 🟣 4. STATUS RED (Rango Operativo)
            // Lógica dinámica: Si ha vaciado más de 20 botes o movido 1000kg es ORO.
            String rango;
            int eficiencia;
            if (vaciados > 10 || totalKgGestionados > 1000) {
                rango = "OPERARIO ORO";
                eficiencia = 100;
            } else if (vaciados > 3) {
                rango = "OPERARIO PLATA";
                eficiencia = 75;
            } else {
                rango = "OPERARIO BRONCE";
                eficiencia = 40;
            }
            dto.setCanjes(rango); // stat4 (Cuadro Púrpura)
            dto.setNombreNivel(rango);
            dto.setProgresoPorcentaje(eficiencia);
        }

        return dto;
    }
}
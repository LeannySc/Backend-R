package com.plasti_usos.reciclaje.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("RECICLADOR")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Reciclador extends Usuario {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "billetera_id")
    @JsonIgnoreProperties("reciclador")
    private BilleteraReciclador billetera;

    @OneToMany(mappedBy = "reciclador", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "reciclador", "punto", "detalles" })
    private List<TransaccionEntrega> historialEntrega = new ArrayList<>();

    @Override
    public List<String> obtenerPermisos() {
        return List.of("VER_MAPA", "REGISTRAR_ENTREGA", "CONSULTAR_CANJE");
    }

    @Override
    public String obtenerTablero() {
        if (billetera != null) {
            return "Puntos Atómicos: " + billetera.getSaldoPuntos() + " | Nivel: " + billetera.getNivelEco();
        }
        return "Iniciando Billetera GTI-3...";
    }

    public int consultarPuntos() {
        return (billetera != null) ? billetera.getSaldoPuntos() : 0;
    }

    public List<TransaccionEntrega> verHistorial() {
        return this.historialEntrega;
    }
    // Dentro de Reciclador.java

    public Integer getSaldoPuntos() {
        return (this.billetera != null) ? this.billetera.getSaldoPuntos() : 0;
    }

    public void setSaldoPuntos(Integer nuevoSaldo) {
        if (this.billetera != null) {
            this.billetera.setSaldoPuntos(nuevoSaldo);
        }
    }

}
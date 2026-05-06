package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data

@DiscriminatorColumn(name = "tipo_usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String correo;

    private String contrasena;

    private String nombre;
    private String apellido;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean verificado = false;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public abstract List<String> obtenerPermisos();

    public abstract String obtenerTablero();

    public String obtenerRol() {
        return this.rol.toString();
    }

    public void IniciarSesion() {
        System.out.println("Iniciando sesión para " + this.correo);

    }

    public boolean actualizarPerfil(String nuevoNombre, String nuevoApellido, String nuevoTelefono,
            String nuevaContrasena) {
        this.nombre = nuevoNombre;
        this.apellido = nuevoApellido;
        this.telefono = nuevoTelefono;
        this.contrasena = nuevaContrasena;

        return true;
    }

    public void cerrarSesion() {
        System.out.println("Cerrando sesión para " + this.correo);

    }

}
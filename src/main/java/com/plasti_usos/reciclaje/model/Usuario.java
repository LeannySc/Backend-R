package com.plasti_usos.reciclaje.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
// @Inheritance(strategy = InheritanceType.JOINED) // Para soportar la familia
// de clases del UML
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Alternativa: usar una sola tabla con un campo discriminador
@DiscriminatorColumn(name = "tipo_usuario") // Para diferenciar entre Reciclador y Encargado
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String correo;
    private String contrasena;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;
    @Column(nullable = false, columnDefinition = "boolean default false") // El usuario empieza bloqueado hasta que se
                                                                          // verifique su correo
    private boolean verificado = false;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private String codigoVerificacion;

    public abstract List<String> obtenerPermisos(); // Método abstracto para definir permisos
    // según el tipo de usuario

    public abstract String obtenerTablero(); // Método abstracto para definir el tablero de control según el tipo de
                                             // usuario

    public String obtenerRol() {
        return this.rol.toString();
    }

    public void IniciarSesion() {
        System.out.println("Iniciando sesión para " + this.correo);
        // Lógica de inicio de sesión (puede ser implementada en un servicio)
    }

    public boolean actualizarPerfil(String nuevoNombre, String nuevaContrasena) {
        this.nombre = nuevoNombre;
        this.contrasena = nuevaContrasena;
        // Lógica para guardar los cambios en la base de datos (puede ser implementada
        // en un servicio)
        return true; // Retorna true si la actualización fue exitosa
    }

    public void cerrarSesion() {
        System.out.println("Cerrando sesión para " + this.correo);
        // Lógica de cierre de sesión (puede ser implementada en un servicio)
    }

    // Campos condicionales según el tipo de producto de la Fábrica
    private Integer saldoPuntos; // Se llena solo si es Reciclador
    private Long puntoID; // Se llena solo si es Encargado
}
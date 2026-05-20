package com.plasti_usos.reciclaje.service;

import com.plasti_usos.reciclaje.model.Administrador;
import com.plasti_usos.reciclaje.model.BilleteraReciclador;
import com.plasti_usos.reciclaje.model.EncargadoPunto;
import com.plasti_usos.reciclaje.model.PinVerificacion;
import com.plasti_usos.reciclaje.model.Reciclador;
import com.plasti_usos.reciclaje.model.Rol;
import com.plasti_usos.reciclaje.model.Usuario;
import com.plasti_usos.reciclaje.repository.PinVerificacionRepository;
import com.plasti_usos.reciclaje.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UsuarioService {
    @Autowired
    private NotificacionService notificador;

    private final EmailService emailService;
    private final PinVerificacionRepository pinRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    UsuarioService(EmailService emailService,
            UsuarioRepository usuarioRepository,
            PinVerificacionRepository pinRepository) {
        this.emailService = emailService;
        this.usuarioRepository = usuarioRepository;
        this.pinRepository = pinRepository;
    }

    @Transactional
    public Usuario fabricarUsuario(Rol tipo, Map<String, Object> datos) {
        Usuario nuevoUsuario;

        if (tipo == Rol.RECICLADOR) {
            Reciclador reciclador = new Reciclador();

            BilleteraReciclador billetera = new BilleteraReciclador();
            billetera.setSaldoPuntos(0);
            billetera.setKilosAportados(0.0);
            billetera.setNivelEco("BRONCE");

            billetera.setReciclador(reciclador);
            reciclador.setBilletera(billetera);

            nuevoUsuario = reciclador;

            System.out.println("[FACTORY] Protocolo: RECICLADOR con Billetera asociada generado con éxito.");
        } else if (tipo == Rol.ENCARGADO) {
            System.out.println("[FACTORY] Creando instancia concreta: ENCARGADO_PUNTO");
            // System.out.println("[FACTORY] Instanciando Producto Concreto:
            // ENCARGADO_PUNTO");
            EncargadoPunto e = new EncargadoPunto();
            // String idString = obtenerDatoSeguro(datos, "puntoID", "0");
            // e.setPuntoID(Long.valueOf(idString));
            e.setLatitudActual(2.4419);
            e.setLongitudActual(-76.6063);
            e.setBotesVaciosTotales(0);
            nuevoUsuario = e;
        } else {
            nuevoUsuario = new Administrador();
            System.out.println("[FACTORY] Creando instancia concreta: ADMINISTRADOR");
        }
        nuevoUsuario.setNombre(obtenerDatoSeguro(datos, "nombre", "Sin Nombre"));
        nuevoUsuario.setApellido(obtenerDatoSeguro(datos, "apellido", "sin apellido"));
        nuevoUsuario.setTelefono(obtenerDatoSeguro(datos, "telefono", "0000000000"));
        nuevoUsuario.setCorreo(obtenerDatoSeguro(datos, "correo", "error@plastiusos.com"));
        nuevoUsuario.setContrasena(obtenerDatoSeguro(datos, "contrasena", "1234"));
        nuevoUsuario.setRol(tipo);
        nuevoUsuario.setVerificado(false);

        if (usuarioRepository.findByCorreo(nuevoUsuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        String pin = String.valueOf((int) (Math.random() * 899999) + 100000);
        PinVerificacion pinEntity = new PinVerificacion();
        pinEntity.setCodigo(pin);
        pinEntity.setUsuario(usuarioGuardado);
        pinEntity.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        pinEntity.setUsado(false);
        pinRepository.save(pinEntity);
        System.out.println("[GTI-SYSTEM] PIN de verificación (" + pin + ") generado y enviado a: "
                + usuarioGuardado.getCorreo());
        emailService.enviarPINVerificacion(usuarioGuardado.getCorreo(), usuarioGuardado.getNombre(), pin);
        // nuevoUsuario.setCodigoVerificacion(pin);
        // System.out.println("[FACTORY] Código de verificación generado: " + pin);
        // nuevoUsuario.setVerificado(false);
        // String correoInput = obtenerDatoSeguro(datos, "correo", "");
        // emailService.enviarPINVerificacion(nuevoUsuario.getCorreo(),
        // nuevoUsuario.getNombre(), pin);
        // return usuarioRepository.save(nuevoUsuario);
        return usuarioGuardado;
    }

    public Usuario registrarNuevoUsuario(Usuario datos) {
        if (datos instanceof Reciclador) {
            System.out.println("[REGISTRO] Inicializando saldo para RECICLADOR");
            ((Reciclador) datos).setSaldoPuntos(0);
        }
        return usuarioRepository.save(datos);
    }

    public Usuario login(String correo, String contrasena) {
        Usuario user = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas o usuario no existe"));

        if (!user.isVerificado()) {
            throw new RuntimeException("Cuenta no verificada. Por favor, verifica tu correo antes de iniciar sesión.");
        }

        if (user.getContrasena().equals(contrasena)) {
            System.out.println("[LOGIN] Usuario autenticado: " + correo);
            return user;
        } else {
            System.out.println("[LOGIN] Contraseña incorrecta para: " + correo);
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    @Transactional
    public boolean modificarPerfil(Long id, String nuevoNombre, String nuevaContrasena, String nuevoApellido,
            String nuevoTelefono) {
        return usuarioRepository.findById(id).map(u -> {
            System.out.println("[DATABASE] Guardando cambios para: " + u.getCorreo());
            u.setNombre(nuevoNombre);
            u.setApellido(nuevoApellido);
            u.setTelefono(nuevoTelefono);

            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                u.setContrasena(nuevaContrasena);
            }

            usuarioRepository.save(u);
            System.out.println("✅ [DATABASE] Datos guardados físicamente en Postgres para ID: " + id);
            return true;
        }).orElse(false);
    }

    private String obtenerDatoSeguro(Map<String, Object> datos, String llave, String valorDefecto) {
        return (datos.get(llave) != null) ? datos.get(llave).toString() : valorDefecto;
    }

    @Transactional
    public boolean validarCuenta(String correo, String codigo) {
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no detectado"));

        return pinRepository.findByCodigo(codigo).map(pin -> {
            if (pin.getUsuario().getId().equals(u.getId()) && !pin.isUsado()) {
                u.setVerificado(true);
                pin.setUsado(true);

                usuarioRepository.save(u);
                pinRepository.save(pin);

                // ✅ AQUÍ LA MEJORA: Solo eliminamos una vez el PIN y disparamos la notificación
                pinRepository.deleteByUsuarioId(u.getId());

                notificador.enviar(
                        u,
                        "Cuenta verificada exitosamente",
                        "Tu cuenta GTI-3 fue activada",
                        "Inicia tu impacto en Popayán",
                        "UsuarioService.java",
                        true);

                System.out.println("✅ [SECURITY] Cuenta blindada para: " + correo);
                return true;
            }
            System.err.println("❌ [SECURITY] Intento de validación fallido para: " + correo);
            return false;
        }).orElse(false);
    }
}

package com.plasti_usos.reciclaje.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarPINVerificacion(String destino, String nombre, String codigo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destino);
            helper.setSubject("🔒 Código de Identidad GTI-3 - PlastiUsos");

            String htmlContent = String.format(
                    "<div style='font-family: sans-serif; border: 1px solid #e5e7eb; border-radius: 20px; padding: 40px; max-width: 500px;'>"
                            +
                            "  <h2 style='color: #10b981; text-transform: uppercase;'>PLASTIUSOS V2.0</h2>" +
                            "  <p>Hola <strong>%s</strong>,</p>" +
                            "  <p>Has iniciado el protocolo de registro en la red industrial de Popayán. Tu código de acceso es:</p>"
                            +
                            "  <div style='background: #f9fafb; padding: 20px; text-align: center; border-radius: 15px; margin: 30px 0;'>"
                            +
                            "    <h1 style='letter-spacing: 10px; color: #111827; margin: 0;'>%s</h1>" +
                            "  </div>" +
                            "  <p style='font-size: 12px; color: #6b7280; font-style: italic;'>" +
                            "    Este código es personal e intransferible. Si no solicitaste este acceso, ignora este mensaje."
                            +
                            "  </p>" +
                            "</div>",
                    nombre, codigo);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("✅ [SMTP] Correo enviado exitosamente a: " + destino);

        } catch (MessagingException e) {
            System.err.println("❌ [SMTP] Fallo en despacho de correo: " + e.getMessage());
            throw new RuntimeException("Error en servidor de mensajería");
        }
    }
}
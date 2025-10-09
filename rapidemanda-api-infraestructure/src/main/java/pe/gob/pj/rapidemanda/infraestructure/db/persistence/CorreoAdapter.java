package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.port.files.CorreoPort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;

@Component("correoPort")
public class CorreoAdapter implements CorreoPort {

    private final JavaMailSender mailSender;

    public CorreoAdapter(@Qualifier("javaMailSender") JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviar(String cuo, String destino, String asunto, String contenidoHtml, String contenidoTexto) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        // habilitar multipart para incluir versión alternativa en texto plano
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(ProjectProperties.getMailFrom());
        helper.setTo(destino);
        helper.setSubject(asunto);
        // setText(plainText, htmlText) usa multipart/alternative
        helper.setText(contenidoTexto, contenidoHtml);
        mailSender.send(message);
    }
}
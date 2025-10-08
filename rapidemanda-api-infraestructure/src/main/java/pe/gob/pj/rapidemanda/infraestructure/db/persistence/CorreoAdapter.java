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
    public void enviar(String cuo, String destino, String asunto, String contenidoHtml) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setFrom(ProjectProperties.getMailFrom());
        helper.setTo(destino);
        helper.setSubject(asunto);
        helper.setText(contenidoHtml, true);
        mailSender.send(message);
    }
}
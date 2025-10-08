package pe.gob.pj.rapidemanda.infraestructure.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;

@Configuration
@DependsOn("projectProperties")
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(ProjectProperties.getMailHost());
        mailSender.setPort(ProjectProperties.getMailPort());
        mailSender.setUsername(ProjectProperties.getMailUsername());
        mailSender.setPassword(ProjectProperties.getMailPassword());

        // Log de verificación de configuración cargada (sin exponer contraseña)
        boolean passwordSet = ProjectProperties.getMailPassword() != null && !ProjectProperties.getMailPassword().isBlank();
        log.info("MailConfig init: host={}, port={}, username={}, auth={}, starttls={}, passwordSet={}",
                ProjectProperties.getMailHost(),
                ProjectProperties.getMailPort(),
                ProjectProperties.getMailUsername(),
                ProjectProperties.isMailAuth(),
                ProjectProperties.isMailStartTls(),
                passwordSet);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", String.valueOf(ProjectProperties.isMailAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(ProjectProperties.isMailStartTls()));
        props.put("mail.debug", "false");

        return mailSender;
    }
}
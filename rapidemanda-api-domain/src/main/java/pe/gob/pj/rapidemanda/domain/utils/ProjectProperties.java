package pe.gob.pj.rapidemanda.domain.utils;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources(value= {
		@PropertySource("classpath:rapidemanda-api.properties")
})
public class ProjectProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String seguridadSecretToken;
    private static Integer seguridadIdAplicativo;
    private static Integer seguridadTiempoExpiraSegundos;
    private static Integer seguridadTiempoRefreshSegundos;
    private static int timeoutBdTransactionSegundos;
    private static int timeoutClientApiConectionSegundos;
    private static int timeoutClientApiReadSegundos;
    private static String captchaUrl;
    private static String captchaToken;
    
    // Parámetros de Alfresco
    private static String alfrescoHost;
    private static String alfrescoPuerto;
    private static String alfrescoUsuario;
    private static String alfrescoClave;
    private static String alfrescoPath;
    private static String alfrescoVersion;

    // Parámetros de correo y URL base
    private static String mailHost;
    private static int mailPort;
    private static String mailUsername;
    private static String mailPassword;
    private static String mailFrom;
    private static boolean mailAuth;
    private static boolean mailStartTls;
    private static String appBaseUrl;

    @Autowired
    public ProjectProperties(
            @Value("${configuracion.seguridad.secretToken:null}") String seguridadSecretToken,
            @Value("${configuracion.seguridad.idaplicativo:0}") Integer seguridadIdAplicativo,
            @Value("${configuracion.seguridad.authenticate.token.tiempo.expira.segundos:300}") Integer seguridadTiempoExpiraSegundos,
            @Value("${configuracion.seguridad.authenticate.token.tiempo.refresh.segundos:180}") Integer seguridadTiempoRefreshSegundos,
            @Value("${timeout.database.transaction.segundos:120}") int timeoutBdTransactionSegundos,
            @Value("${timeout.client.api.conection.segundos:60}") int timeoutClientApiConectionSegundos,
            @Value("${timeout.client.api.read.segundos:60}") int timeoutClientApiReadSegundos,
        	@Value("${captcha.url:null}") String captchaUrl,
        	@Value("${captcha.token:null}") String captchaToken,
        	//ALFRESCO
        	@Value("${alfresco.rapidemanda.host:localhost}") String alfrescoHost,
        	@Value("${alfresco.rapidemanda.puerto:8080}") String alfrescoPuerto,
        	@Value("${alfresco.rapidemanda.usuario:admin}") String alfrescoUsuario,
        	@Value("${alfresco.rapidemanda.clave:admin}") String alfrescoClave,
        	@Value("${alfresco.rapidemanda.path:/app:company_home/cm:RAPIDEMANDA}") String alfrescoPath,
            @Value("${alfresco.rapidemanda.version:4.2}") String alfrescoVersion,
            //EMAIL
            @Value("${mail.smtp.host:localhost}") String mailHost,
            @Value("${mail.smtp.port:25}") int mailPort,
            @Value("${mail.smtp.username:}") String mailUsername,
            @Value("${mail.smtp.password:}") String mailPassword,
            @Value("${mail.smtp.from:noreply@rapidemanda.gob.pe}") String mailFrom,
            @Value("${mail.smtp.auth:true}") boolean mailAuth,
            @Value("${mail.smtp.starttls.enable:true}") boolean mailStartTls,
            @Value("${configuracion.app.baseUrl:http://localhost:4200}") String appBaseUrl) {

        ProjectProperties.seguridadSecretToken = seguridadSecretToken;
        ProjectProperties.seguridadIdAplicativo = seguridadIdAplicativo;
        ProjectProperties.seguridadTiempoExpiraSegundos = seguridadTiempoExpiraSegundos;
        ProjectProperties.seguridadTiempoRefreshSegundos = seguridadTiempoRefreshSegundos;
        ProjectProperties.timeoutBdTransactionSegundos = timeoutBdTransactionSegundos;
        ProjectProperties.timeoutClientApiConectionSegundos = timeoutClientApiConectionSegundos;
        ProjectProperties.timeoutClientApiReadSegundos = timeoutClientApiReadSegundos;
        ProjectProperties.captchaUrl = captchaUrl;
        ProjectProperties.captchaToken = captchaToken;
        
        //ALFRESCO
        ProjectProperties.alfrescoHost = alfrescoHost;
        ProjectProperties.alfrescoPuerto = alfrescoPuerto;
        ProjectProperties.alfrescoUsuario = alfrescoUsuario;
        ProjectProperties.alfrescoClave = alfrescoClave;
        ProjectProperties.alfrescoPath = alfrescoPath;
        ProjectProperties.alfrescoVersion = alfrescoVersion;
        
        //EMAIL
        ProjectProperties.mailHost = mailHost;
        ProjectProperties.mailPort = mailPort;
        ProjectProperties.mailUsername = mailUsername;
        ProjectProperties.mailPassword = mailPassword;
        ProjectProperties.mailFrom = mailFrom;
        ProjectProperties.mailAuth = mailAuth;
        ProjectProperties.mailStartTls = mailStartTls;
        ProjectProperties.appBaseUrl = appBaseUrl;
    }

	public static String getSeguridadSecretToken() {
		return seguridadSecretToken;
	}

	public static Integer getSeguridadIdAplicativo() {
		return seguridadIdAplicativo;
	}

	public static Integer getSeguridadTiempoExpiraSegundos() {
		return seguridadTiempoExpiraSegundos;
	}

	public static Integer getSeguridadTiempoRefreshSegundos() {
		return seguridadTiempoRefreshSegundos;
	}

	public static int getTimeoutBdTransactionSegundos() {
		return timeoutBdTransactionSegundos;
	}

	public static int getTimeoutClientApiConectionSegundos() {
		return timeoutClientApiConectionSegundos;
	}

	public static int getTimeoutClientApiReadSegundos() {
		return timeoutClientApiReadSegundos;
	}

	public static String getCaptchaUrl() {
		return captchaUrl;
	}

	public static String getCaptchaToken() {
		return captchaToken;
	}

	public static String getAlfrescoHost() {
		return alfrescoHost;
	}

	public static String getAlfrescoPuerto() {
		return alfrescoPuerto;
	}

	public static String getAlfrescoUsuario() {
		return alfrescoUsuario;
	}

	public static String getAlfrescoClave() {
		return alfrescoClave;
	}

	public static String getAlfrescoPath() {
		return alfrescoPath;
	}

    public static String getAlfrescoVersion() {
        return alfrescoVersion;
    }

    public static String getMailHost() {
        return mailHost;
    }

    public static int getMailPort() {
        return mailPort;
    }

    public static String getMailUsername() {
        return mailUsername;
    }

    public static String getMailPassword() {
        return mailPassword;
    }

    public static String getMailFrom() {
        return mailFrom;
    }

    public static boolean isMailAuth() {
        return mailAuth;
    }

    public static boolean isMailStartTls() {
        return mailStartTls;
    }

    public static String getAppBaseUrl() {
        return appBaseUrl;
    }

}

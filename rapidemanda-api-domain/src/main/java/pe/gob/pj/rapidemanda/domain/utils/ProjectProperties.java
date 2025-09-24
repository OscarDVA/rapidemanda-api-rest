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
        	@Value("${alfresco.rapidemanda.host:localhost}") String alfrescoHost,
        	@Value("${alfresco.rapidemanda.puerto:8080}") String alfrescoPuerto,
        	@Value("${alfresco.rapidemanda.usuario:admin}") String alfrescoUsuario,
        	@Value("${alfresco.rapidemanda.clave:admin}") String alfrescoClave,
        	@Value("${alfresco.rapidemanda.path:/app:company_home/cm:RAPIDEMANDA}") String alfrescoPath,
        	@Value("${alfresco.rapidemanda.version:4.2}") String alfrescoVersion) {

        ProjectProperties.seguridadSecretToken = seguridadSecretToken;
        ProjectProperties.seguridadIdAplicativo = seguridadIdAplicativo;
        ProjectProperties.seguridadTiempoExpiraSegundos = seguridadTiempoExpiraSegundos;
        ProjectProperties.seguridadTiempoRefreshSegundos = seguridadTiempoRefreshSegundos;
        ProjectProperties.timeoutBdTransactionSegundos = timeoutBdTransactionSegundos;
        ProjectProperties.timeoutClientApiConectionSegundos = timeoutClientApiConectionSegundos;
        ProjectProperties.timeoutClientApiReadSegundos = timeoutClientApiReadSegundos;
        ProjectProperties.captchaUrl = captchaUrl;
        ProjectProperties.captchaToken = captchaToken;
        ProjectProperties.alfrescoHost = alfrescoHost;
        ProjectProperties.alfrescoPuerto = alfrescoPuerto;
        ProjectProperties.alfrescoUsuario = alfrescoUsuario;
        ProjectProperties.alfrescoClave = alfrescoClave;
        ProjectProperties.alfrescoPath = alfrescoPath;
        ProjectProperties.alfrescoVersion = alfrescoVersion;
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

}

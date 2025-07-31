package pe.gob.pj.rapidemanda.domain.utils;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.model.client.CaptchaValid;



@Slf4j
public class CaptchaUtils {
	
	public static final boolean validCaptcha(String token, String remoteIp, String cuo) {
		try {
			
			String URL = ProjectProperties.getCaptchaUrl();
			String TOKEN = ProjectProperties.getCaptchaToken();
			
			RestTemplate plantilla = new RestTemplate();			
			UriComponents builder = UriComponentsBuilder.fromHttpUrl(URL)
		                .queryParam("secret", TOKEN)
		                .queryParam("response", token)
		                .queryParam("remoteip", remoteIp).build();
			CaptchaValid resultado = plantilla.postForObject(builder.toUriString(), null, CaptchaValid.class);
			log.info("{} {} : {}", cuo, builder.toUriString(), resultado.getSuccess());
			if(resultado.getSuccess().equals("true")) {
				return Boolean.TRUE;
			}
		} catch (Exception e) {
			log.error("{} {}", cuo, ProjectUtils.convertExceptionToString(e));
		}
		return Boolean.FALSE;
	}

}

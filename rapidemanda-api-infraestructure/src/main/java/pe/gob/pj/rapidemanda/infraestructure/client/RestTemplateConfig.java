package pe.gob.pj.rapidemanda.infraestructure.client;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;

@Configuration
public class RestTemplateConfig {
	
	@Bean
    public RestTemplate restTemplate() throws IOException {
        return new RestTemplate(clientHttpRequestFactory());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() throws IOException {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(ProjectProperties.getTimeoutClientApiConectionSegundos()*1000);
        factory.setReadTimeout(ProjectProperties.getTimeoutClientApiReadSegundos()*1000);
        return factory;
    }
}

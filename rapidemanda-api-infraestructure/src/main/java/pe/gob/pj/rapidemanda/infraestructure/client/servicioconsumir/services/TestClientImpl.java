package pe.gob.pj.rapidemanda.infraestructure.client.servicioconsumir.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service("sunarpClient")
public class TestClientImpl implements TestClient{
	
	@SuppressWarnings("unused")
	@Autowired
    private RestTemplate restTemplate;

}

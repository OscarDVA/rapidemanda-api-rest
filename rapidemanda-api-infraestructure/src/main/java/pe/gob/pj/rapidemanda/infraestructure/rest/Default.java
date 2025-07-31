package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public interface Default extends Base{
	
	/**
	 * Método que sirve para verificar versión actual del aplicativo
	 * 
	 * @param cuo Código único de log
	 * @return Datos del aplicativo
	 */
	@GetMapping(value = "/healthcheck")
	public ResponseEntity<Object> healthcheck(@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);
	
	/**
	 * MÉTODO QUE GENERA NUEVO TOKEN A PARTIR DE TOKEN ANTERIOR
	 * 
	 * @param token           es token antentior
	 * @param ipRemota        es la ip desde donde lo solicita
	 * @param tokenAdmin      es el token de la seccion administrador
	 * @param validTokenAdmin indicador si necesitamos validar token del admin
	 * @param cuo             código único de log
	 * @return un nuevo token
	 */
	@GetMapping(value = "/seguridad/refresh")
	public ResponseEntity<GlobalResponse> refreshToken(@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo, 
			@RequestAttribute(name=ProjectConstants.AUD_IP) String ipRemota,
			@RequestParam(required = true) String token);
	
}

package pe.gob.pj.rapidemanda.infraestructure.rest;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.UsuarioRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequestMapping(value = "usuarios", produces = {
		MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionUsuario extends Base{
	
	
	/***
	 * 
	 * GET /usuarios : Buscar usuario en base a los filtros enviados
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param formatoRespuesta
	 * @param usuario
	 * @return
	 */
	@GetMapping
	public ResponseEntity<GlobalResponse> consultarUsuarios(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
			@RequestParam(name = "usuario", required = false) String usuario);

	/***
	 * 
	 * POST /usuarios/registrar : Crear usuario 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param usuario
	 * @return
	 */
	@PostMapping(value="crear")
	public ResponseEntity<GlobalResponse> crearUsuario(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@Validated @RequestBody UsuarioRequest usuario);
}

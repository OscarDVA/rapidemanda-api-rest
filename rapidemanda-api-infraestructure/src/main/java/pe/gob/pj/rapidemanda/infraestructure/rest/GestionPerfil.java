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
@RequestMapping(value = "perfiles", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionPerfil extends Base {

	/**
	 * GET /perfiles : Buscar perfiles en base a los filtros enviados
	 * 
	 * @param cuo Código único de operación
	 * @param ips IP del servidor
	 * @param usuauth Usuario autenticado
	 * @param uri URI de la petición
	 * @param params Parámetros de la petición
	 * @param herramienta Herramienta utilizada
	 * @param ip IP del cliente
	 * @param formatoRespuesta Formato de respuesta (json/xml)
	 * @param activo Filtro por estado activo (opcional)
	 * @param id Filtro por ID de perfil (opcional)
	 * @return Lista de perfiles que coinciden con los filtros
	 */
	@GetMapping
	public ResponseEntity<GlobalResponse> consultarPerfiles(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);
}
package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequestMapping(value = "demandas", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionDemanda extends Base {
	/**
	 * GET /demandas : Listar las demadas por id
	 *
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param formatoRespuesta
	 * @param id
	 * @return ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<GlobalResponse> consultarDemandas(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "idEstadoDemanda", required = false) String bEstadoId,
			@RequestParam(name = "idUsuario", required = false) Integer idUsuario);

	/***
	 * 
	 * POST /demanda/crear : Crear demanda en base a los datos enviados
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param demanda
	 * @return
	 */
	@PostMapping(value = "crear")
	public ResponseEntity<GlobalResponse> registrarDemanda(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip, @RequestBody DemandaRequest demanda);

	/**
	 * PUT /demanda/actualizar : Actualizar demanda en base al id
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param demanda
	 * @return
	 */
	@PutMapping(value = "actualizar/{id}")
	public ResponseEntity<GlobalResponse> actualizarDemanda(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip, @PathVariable(name = "id") Integer id,
			@Validated @RequestBody DemandaRequest demanda);
	
	 @DeleteMapping("/{id}")
	    ResponseEntity<GlobalResponse> eliminarDemanda(
	            @RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
	            @RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
	            @RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
	            @RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
	            @RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
	            @RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
	            @RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
	            @PathVariable("id") Integer id,
	            @RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);
}

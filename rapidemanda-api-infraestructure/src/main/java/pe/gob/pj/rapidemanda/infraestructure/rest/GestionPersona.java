package pe.gob.pj.rapidemanda.infraestructure.rest;

import jakarta.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.PersonaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequestMapping(value = "personas", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionPersona extends Base {

	/***
	 * 
	 * GET /personas : Consultar datos de persona
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param formatoRespuesta
	 * @param numeroDocumento
	 * @return
	 */
	@GetMapping
	public ResponseEntity<GlobalResponse> consultarPersonas(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
			@Length(min = 8, max = 8, message = "El parámetro numero_documento tiene un tamaño no valido [min1,max=1].") @Pattern(regexp = ProjectConstants.Pattern.NUMBER, message = "El parámetro numero_documento solo permite valores numéricos.") @RequestParam(name = "numero_documento", required = false) String numeroDocumento);

	/***
	 * 
	 * POST /personas/crear : Crear persona en base a los datos enviados
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param persona
	 * @return
	 */
	@PostMapping(value = "crear")
	public ResponseEntity<GlobalResponse> registrarPersona(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@Validated @RequestBody PersonaRequest persona);

	/***
	 * 
	 * PUT /personas/actualizar/{id} : Actualizar persona en base al id
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param id
	 * @param persona
	 * @return
	 */
	@PutMapping(value = "actualizar/{id}")
	public ResponseEntity<GlobalResponse> actualizarPersona(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip, @PathVariable(name = "id") Integer id,
			@Validated @RequestBody PersonaRequest persona);

	/***
	 * 
	 * PATCH /personas/{id}/estado : Cambiar estado activo de una persona
	 * 
	 * @param cuo
	 * @param ips
	 * @param usuauth
	 * @param uri
	 * @param params
	 * @param herramienta
	 * @param ip
	 * @param id
	 * @param activo
	 * @return
	 */
	@PatchMapping(value = "actualizar/estado/{id}")
	public ResponseEntity<GlobalResponse> cambiarEstadoPersona(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip, @PathVariable(name = "id") Integer id,
			@RequestParam(name = "activo") String activo);
}

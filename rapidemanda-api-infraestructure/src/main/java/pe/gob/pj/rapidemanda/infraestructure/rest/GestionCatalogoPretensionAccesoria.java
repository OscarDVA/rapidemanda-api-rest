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
@RequestMapping(value = "catalogo-pretension-accesoria", produces = { MediaType.APPLICATION_JSON_VALUE,
		MediaType.APPLICATION_XML_VALUE })
public interface GestionCatalogoPretensionAccesoria extends Base {

	@GetMapping
	ResponseEntity<GlobalResponse> consultarCatalogoPretensionesAccesorias(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
			@RequestParam(name = "id_concepto", required = false) Integer conceptoId,
			@RequestParam(name = "id_pretension_principal", required = false) Integer pretensionPrincipalId);
}
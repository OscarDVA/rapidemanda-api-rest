package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.hibernate.validator.constraints.Length;
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
@RequestMapping(value = "distritos", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionDistrito extends Base {

	@GetMapping
	public ResponseEntity<GlobalResponse> consultarDistritos(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
			@Length(min = 4, max = 4, message = "El parámetro codigo de Provincia tiene un tamaño no valido [min4,max=4].") @RequestParam(name = "id_provincia", required = false) String idProvincia,
			@Length(min = 6, max = 6, message = "El parámetro código de Distrito tiene un tamaño no válido [min=6,max=6].") @RequestParam(name = "id_distrito", required = false) String idDistrito);
}

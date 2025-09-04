package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;
import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDistritoUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionDistritoController implements GestionDistrito, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Qualifier("gestionDistritoUseCasePort")
	final GestionDistritoUseCasePort gestionDistritoUseCasePort;

	@Override
	public ResponseEntity<GlobalResponse> consultarDistritos(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip,String formatoRespuesta, String idProvincia, String idDistrito) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		
		try {
			Map<String, Object> filters = new HashMap<String, Object>();
			
			if (idProvincia != null && !idProvincia.trim().isEmpty()) {
				filters.put(Provincia.P_PROVINCIA_ID, idProvincia);
			}
			if (idDistrito != null && !idDistrito.trim().isEmpty()) {
				filters.put(Distrito.P_DISTRITO_ID, idDistrito);
			}
			
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			filters.put(Distrito.P_PROVINCIA_ID, idProvincia);
			res.setData(gestionDistritoUseCasePort.buscarDistrito(cuo, filters));
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(),Proceso.DISTRITO_CONSULTAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE : MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

}

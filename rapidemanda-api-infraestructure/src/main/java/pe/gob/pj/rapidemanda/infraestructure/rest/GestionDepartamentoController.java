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
import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDepartamentoUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionDepartamentoController implements GestionDepartamento, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Qualifier("gestionDepartamentoUseCasePort")
	final GestionDepartamentoUseCasePort gestionDepartamentoUseCasePort;

	@Override
	public ResponseEntity<GlobalResponse> consultarDepartamentos(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip,String formatoRespuesta, String idDepartamento) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		
		try {
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Map<String, Object> filters = new HashMap<String, Object>();
			filters.put(Departamento.P_DEPARTAMENTO_ID, idDepartamento);
			res.setData(gestionDepartamentoUseCasePort.buscarDepartamento(cuo, filters));
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(),Proceso.DEPARTAMENTO_CONSULTAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE : MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

}

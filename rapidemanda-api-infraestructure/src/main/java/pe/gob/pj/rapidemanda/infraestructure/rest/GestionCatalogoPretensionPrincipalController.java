package pe.gob.pj.rapidemanda.infraestructure.rest;

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
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionPrincipalUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionCatalogoPretensionPrincipalController implements GestionCatalogoPretensionPrincipal, Serializable {

	private static final long serialVersionUID = 1L;

	@Qualifier("gestionPretensionPrincipalUseCasePort")
	final GestionPretensionPrincipalUseCasePort gestionPretensionPrincipalUseCasePort;

	@Override
	public ResponseEntity<GlobalResponse> consultarCatalogoPretensionPrincipales(String cuo, String ips, String usuauth,
			String uri, String params, String herramienta, String ip, String formatoRespuesta, Integer petitorioId) {

		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);

		try {
			
			Map<String, Object> filters = new HashMap<String, Object>();
						
			filters.put(CatalogoPretensionPrincipal.P_PETITORIO_ID, petitorioId);
			res.setData(gestionPretensionPrincipalUseCasePort.buscarPretensionPrincipal(cuo, filters));
			
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());

		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(),
							Proceso.CATALOGO_PRETENSION_PRINCIPAL_CONSULTAR.getNombre()),
					e.getMessage(), e.getCause()), res);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));

		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}
}

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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.usecase.AuditoriaGeneralUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionUsuarioUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.mapper.AuditoriaGeneralMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.UsuarioMapper;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.UsuarioRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionUsuarioController implements GestionUsuario, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Qualifier("gestionUsuarioUseCasePort")
	final GestionUsuarioUseCasePort gestionUsuarioUseCasePort;
	final AuditoriaGeneralUseCasePort auditoriaGeneralUseCasePort;
	final UsuarioMapper usuarioMapper;
	final AuditoriaGeneralMapper auditoriaGeneralMapper;
	
	@Override
	public ResponseEntity<GlobalResponse> consultarUsuarios(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip,String formatoRespuesta, String usuario) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		
		try {
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Map<String, Object> filters = new HashMap<String, Object>();
			filters.put(Usuario.P_NOMBRE_USUARIO, usuario);
			res.setData(gestionUsuarioUseCasePort.buscarUsuario(cuo, filters));
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(),Proceso.PERSONA_CONSULTAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE : MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> registrarUsuario(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, UsuarioRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			long inicio = System.currentTimeMillis();
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Usuario usuarioDto = usuarioMapper.toUsuario(request);
			gestionUsuarioUseCasePort.registrarUsuario(cuo, usuarioDto);
			res.setData(usuarioDto);
			long fin = System.currentTimeMillis();
			AuditoriaAplicativos auditoriaAplicativos = auditoriaGeneralMapper.toAuditoriaAplicativos(
					request.getAuditoria(), cuo, ips, usuauth, uri, params, herramienta, res.getCodigo(),
					res.getDescripcion(), fin - inicio);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(request);
			auditoriaAplicativos.setPeticionBody(jsonString);
			auditoriaGeneralUseCasePort.crear(cuo, auditoriaAplicativos);
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.USUARIO_REGISTRAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType
				.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(request.getFormatoRespuesta())
						? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

}

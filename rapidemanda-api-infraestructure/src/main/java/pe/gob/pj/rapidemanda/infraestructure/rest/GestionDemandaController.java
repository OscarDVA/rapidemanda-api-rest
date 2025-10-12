package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Date;

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
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.port.usecase.AuditoriaGeneralUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDemandaUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.mapper.AuditoriaGeneralMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandaMapper;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRecepcionRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionDemandaController implements GestionDemanda, Serializable {
	private static final long serialVersionUID = 1L;

	@Qualifier("gestionDemandaUseCasePort")
	final GestionDemandaUseCasePort gestionDemandaUseCasePort;
	final AuditoriaGeneralUseCasePort auditoriaGeneralUseCasePort;
	final DemandaMapper demandaMapper;
	final AuditoriaGeneralMapper auditoriaGeneralMapper;

	@Override
	public ResponseEntity<GlobalResponse> consultarDemandas(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, String formatoRespuesta, Integer id, String bEstadoId,
			Integer idUsuario, String idTipoPresentacion, String tipoRecepcion, Integer idUsuarioRecepcion,
			String fechaCompletadoInicio, String fechaCompletadoFin) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);

		try {

			Map<String, Object> filters = new HashMap<String, Object>();

			if (id != null) {
				filters.put(Demanda.P_ID, id);
			}
            // Parseo y manejo de estados (acepta múltiples: "C,P")
            List<String> estadosFiltro = null;
            if (bEstadoId != null && !bEstadoId.trim().isEmpty()) {
                estadosFiltro = Arrays.stream(bEstadoId.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();
            }
			if (idUsuario != null) {
				filters.put(Demanda.P_USUARIO, idUsuario);
			}

			if (idTipoPresentacion != null && !idTipoPresentacion.trim().isEmpty()) {
				filters.put(Demanda.P_TIPO_PRESENTACION, idTipoPresentacion);
			}
			if (tipoRecepcion != null && !tipoRecepcion.trim().isEmpty()) {
				filters.put(Demanda.P_TIPO_RECEPCION, tipoRecepcion);
			}
			if (idUsuarioRecepcion != null) {
				filters.put(Demanda.P_USUARIO_RECEPCION, idUsuarioRecepcion);
			}

            // Filtros por rango de fechaCompletado (dd/MM/yyyy)
            try {
                if (fechaCompletadoInicio != null && ProjectUtils.esFechaValida(fechaCompletadoInicio,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date inicio = ProjectUtils.parseStringToDate(fechaCompletadoInicio,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    filters.put(Demanda.P_FECHA_COMPLETADO_INICIO, inicio);
                }
                if (fechaCompletadoFin != null && ProjectUtils.esFechaValida(fechaCompletadoFin,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date fin = ProjectUtils.parseStringToDate(fechaCompletadoFin,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    // Incluir todo el día (23:59:59)
                    fin = ProjectUtils.sumarRestarSegundos(fin, 86399);
                    filters.put(Demanda.P_FECHA_COMPLETADO_FIN, fin);
                }
            } catch (Exception ex) {
                // Ignorar errores de parseo y no aplicar filtros de fecha
            }

            // Considerar estados C y P para filtro por fechas; excluir B
            boolean tieneRangoFechas = filters.containsKey(Demanda.P_FECHA_COMPLETADO_INICIO)
                    && filters.containsKey(Demanda.P_FECHA_COMPLETADO_FIN);

            if (tieneRangoFechas) {
                if (estadosFiltro == null || estadosFiltro.isEmpty()) {
                    // Si no se envían estados, por defecto aplicar C y P
                    filters.put(Demanda.P_ESTADO_IDS, List.of("C", "P"));
                } else {
                    // Excluir B si viene mezclado con otros estados
                    List<String> estadosSinB = estadosFiltro.stream()
                            .filter(e -> !"B".equalsIgnoreCase(e))
                            .toList();
                    if (estadosSinB.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosSinB);
                    } else if (estadosSinB.size() == 1) {
                        filters.put(Demanda.P_ESTADO_ID, estadosSinB.get(0));
                    }
                    // Si todo eran B, no colocar filtro de estado; el rango por fecha excluye B naturalmente
                }
            } else {
                // Sin rango de fechas, mantener comportamiento original
                if (estadosFiltro != null && !estadosFiltro.isEmpty()) {
                    if (estadosFiltro.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosFiltro);
                    } else {
                        filters.put(Demanda.P_ESTADO_ID, estadosFiltro.get(0));
                    }
                }
            }

			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			res.setData(gestionDemandaUseCasePort.buscarDemandas(cuo, filters));
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_CONSULTAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> registrarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, DemandaRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			long inicio = System.currentTimeMillis();
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Demanda demandaDto = demandaMapper.toDemanda(request);
			gestionDemandaUseCasePort.registrarDemanda(cuo, demandaDto);
			res.setData(demandaDto);
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
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REGISTRAR.getNombre()),
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

	@Override
	public ResponseEntity<GlobalResponse> actualizarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, Integer id, DemandaRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			long inicio = System.currentTimeMillis();
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Demanda demandaDto = demandaMapper.toDemanda(request);
			demandaDto.setId(id);
			gestionDemandaUseCasePort.actualizarDemanda(cuo, demandaDto);
			res.setData(demandaDto);
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
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()),
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

	@Override
	public ResponseEntity<GlobalResponse> eliminarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, Integer id, String formatoRespuesta) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			gestionDemandaUseCasePort.eliminar(cuo, id);
			res.setData("Demanda eliminada exitosamente");
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ELIMINAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> actualizarEstadoRecepcionDemanda(String cuo, String ips, String usuauth,
			String uri, String params, String herramienta, String ip, Integer id, DemandaRecepcionRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			// Validación simple de estado solicitado
			if (request.getNuevoEstadoDemanda() == null || !"P".equals(request.getNuevoEstadoDemanda())) {
				throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(), String
						.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.ESTADO_ACTUALIZAR.getNombre()));
			}

			// Usar siempre la fecha actual del servidor
			Date fecha = new Date();

			gestionDemandaUseCasePort.actualizarCamposDemanda(cuo, id, request.getNuevoEstadoDemanda(),
					request.getTipoRecepcion(), fecha, request.getIdUsuarioRecepcion());

			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion("Demanda actualizada correctamente");
			Map<String, Object> data = new HashMap<>();
			data.put("idDemanda", id);
			data.put("estadoDemanda", request.getNuevoEstadoDemanda());
			data.put("tipoRecepcion", request.getTipoRecepcion());
			data.put("fechaRecepcion",
					ProjectUtils.convertDateToString(fecha, ProjectConstants.Formato.FECHA_DD_MM_YYYY));
			data.put("idUsuarioRecepcion", request.getIdUsuarioRecepcion());
			res.setData(data);
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}
}

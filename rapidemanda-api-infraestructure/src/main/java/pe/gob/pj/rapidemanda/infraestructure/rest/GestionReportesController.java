package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionReportesUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionReportesController  implements GestionReportes,  Serializable {

	private static final long serialVersionUID = 1L;
	
	@Qualifier("gestionReportesUseCasePort") 
	final GestionReportesUseCasePort gestionReportesUseCasePort;

    @Override
    public ResponseEntity<GlobalResponse> consultarPetitoriosPorTipo(String cuo, String ips, String usuauth, String uri, String params, String herramienta, String ip,
            String formatoRespuesta, String fechaCompletadoInicio, String fechaCompletadoFin, String estados) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);
        try {
            Date inicio = parseFecha(fechaCompletadoInicio);
            Date fin = parseFechaFin(fechaCompletadoFin);
            if (inicio == null || fin == null) {
                throw new ErrorException(Errors.PARAMETROS_INCOMPLETOS.getCodigo(),
                        String.format(Errors.PARAMETROS_INCOMPLETOS.getNombre(), Proceso.REPORTES_CONSULTAR.getNombre()));
            }

            List<String> estadosList = parseEstadosParaFechas(estados);

            res.setData(gestionReportesUseCasePort.obtenerConteosPetitorioPorTipo(cuo, inicio, fin, estadosList));
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                    e.getMessage(), e.getCause()), res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GlobalResponse> consultarPetitoriosPorTipoYPrincipal(String cuo, String ips, String usuauth, String uri, String params, String herramienta, String ip,
            String formatoRespuesta, String fechaCompletadoInicio, String fechaCompletadoFin, String estados) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);
        try {
            Date inicio = parseFecha(fechaCompletadoInicio);
            Date fin = parseFechaFin(fechaCompletadoFin);
            if (inicio == null || fin == null) {
                throw new ErrorException(Errors.PARAMETROS_INCOMPLETOS.getCodigo(),
                        String.format(Errors.PARAMETROS_INCOMPLETOS.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()));
            }

            List<String> estadosList = parseEstadosParaFechas(estados);

            res.setData(gestionReportesUseCasePort.obtenerConteosPetitorioPorTipoYPrincipal(cuo, inicio, fin, estadosList));
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                    e.getMessage(), e.getCause()), res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GlobalResponse> consultarRankingPetitoriosPorTipo(String cuo, String ips, String usuauth, String uri, String params, String herramienta, String ip,
            String formatoRespuesta, String fechaCompletadoInicio, String fechaCompletadoFin, String estados) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);
        try {
            Date inicio = parseFecha(fechaCompletadoInicio);
            Date fin = parseFechaFin(fechaCompletadoFin);
            if (inicio == null || fin == null) {
                throw new ErrorException(Errors.PARAMETROS_INCOMPLETOS.getCodigo(),
                        String.format(Errors.PARAMETROS_INCOMPLETOS.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()));
            }

            List<String> estadosList = parseEstadosParaFechas(estados);
            List<ConteoPetitorioTipoItem> conteos = gestionReportesUseCasePort.obtenerConteosPetitorioPorTipo(cuo, inicio, fin, estadosList);
            List<ConteoPetitorioTipoItem> top6 = conteos.stream()
                    .sorted((a, b) -> Long.compare(b.getTotal(), a.getTotal()))
                    .limit(6)
                    .toList();

            res.setData(top6);
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                    e.getMessage(), e.getCause()), res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GlobalResponse> consultarDemandanteConteosPorFechasEstados(String cuo, String ips, String usuauth, String uri, String params, String herramienta, String ip,
            String formatoRespuesta, String fechaCompletadoInicio, String fechaCompletadoFin, String estados) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);
        try {
            Date inicio = parseFecha(fechaCompletadoInicio);
            Date fin = parseFechaFin(fechaCompletadoFin);
            if (inicio == null || fin == null) {
                throw new ErrorException(Errors.PARAMETROS_INCOMPLETOS.getCodigo(),
                        String.format(Errors.PARAMETROS_INCOMPLETOS.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()));
            }

            List<String> estadosList = parseEstadosParaFechas(estados);

            res.setData(gestionReportesUseCasePort.obtenerDemandanteConteosPorFechasEstados(cuo, inicio, fin, estadosList));
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                    e.getMessage(), e.getCause()), res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GlobalResponse> consultarDemandantesPetitorioPorFechasEstados(String cuo, String ips, String usuauth, String uri, String params, String herramienta, String ip,
            String formatoRespuesta, String fechaCompletadoInicio, String fechaCompletadoFin, String estados) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);
        try {
            Date inicio = parseFecha(fechaCompletadoInicio);
            Date fin = parseFechaFin(fechaCompletadoFin);
            if (inicio == null || fin == null) {
                throw new ErrorException(Errors.PARAMETROS_INCOMPLETOS.getCodigo(),
                        String.format(Errors.PARAMETROS_INCOMPLETOS.getNombre(), Proceso.REPORTES_CONSULTAR.getNombre()));
            }

            List<String> estadosList = parseEstadosParaFechas(estados);

            res.setData(gestionReportesUseCasePort.obtenerDemandantesPetitorioPorFechasEstados(cuo, inicio, fin, estadosList));
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo, new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                    e.getMessage(), e.getCause()), res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    private Date parseFecha(String fecha) {
        try {
            if (fecha != null && ProjectUtils.esFechaValida(fecha, ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                return ProjectUtils.parseStringToDate(fecha, ProjectConstants.Formato.FECHA_DD_MM_YYYY);
            }
        } catch (Exception ex) {
            log.debug("Error parseando fecha {}: {}", fecha, ex.getMessage());
        }
        return null;
    }

    private Date parseFechaFin(String fecha) {
        Date d = parseFecha(fecha);
        if (d != null) {
            // incluye todo el día
            return ProjectUtils.sumarRestarSegundos(d, 86399);
        }
        return null;
    }

    private List<String> parseEstadosParaFechas(String estados) {
        List<String> res = new ArrayList<>();
        if (estados != null && !estados.isBlank()) {
            for (String s : estados.split(",")) {
                String v = s.trim().toUpperCase();
                if (!v.isEmpty() && !"B".equals(v)) {
                    res.add(v);
                }
            }
        }
        if (res.isEmpty()) {
            // por defecto C y P para fechas completadas
            res.add("C");
            res.add("P");
        }
        return res;
    }
}
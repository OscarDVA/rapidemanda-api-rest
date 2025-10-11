package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;

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
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDashboardUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionDashboardController implements GestionDashboard, Serializable {

    private static final long serialVersionUID = 1L;

    final GestionDashboardUseCasePort gestionDashboardUseCasePort;

    @Override
    public ResponseEntity<GlobalResponse> consultarResumen(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, String formatoRespuesta) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
            res.setData(gestionDashboardUseCasePort.obtenerResumen(cuo));
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
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
    public ResponseEntity<GlobalResponse> consultarGraficos(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, String formatoRespuesta) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
            res.setData(gestionDashboardUseCasePort.obtenerGraficos(cuo));
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
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
    public ResponseEntity<GlobalResponse> consultarDemandasRecientes(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, String formatoRespuesta, Integer limite) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
            res.setData(gestionDashboardUseCasePort.obtenerDemandasRecientes(cuo, limite));
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
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
    public ResponseEntity<GlobalResponse> consultarPetitorioConteos(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, String formatoRespuesta) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
            res.setData(gestionDashboardUseCasePort.obtenerPetitorioConteos(cuo));
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
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
    public ResponseEntity<GlobalResponse> consultarDemandanteConteos(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, String formatoRespuesta) {
        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
            res.setData(gestionDashboardUseCasePort.obtenerDemandanteConteos(cuo));
        } catch (ErrorException e) {
            handleException(cuo, e, res);
        } catch (Exception e) {
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DASHBOARD_CONSULTAR.getNombre()),
                            e.getMessage(), e.getCause()),
                    res);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
                        : MediaType.APPLICATION_JSON_VALUE));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }
}
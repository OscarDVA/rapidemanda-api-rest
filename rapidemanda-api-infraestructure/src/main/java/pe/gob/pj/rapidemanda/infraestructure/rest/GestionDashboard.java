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
@RequestMapping(value = "dashboard", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionDashboard extends Base {

    /**
     * GET /dashboard/resumen : Obtener el resumen del dashboard
     */
    @GetMapping("/resumen")
    ResponseEntity<GlobalResponse> consultarResumen(
            @RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
            @RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
            @RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
            @RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
            @RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
            @RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
            @RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
            @RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);

    /**
     * GET /dashboard/listas : Obtener listas y conteos para el dashboard
     */
    @GetMapping("/listas")
    ResponseEntity<GlobalResponse> consultarListas(
            @RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
            @RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
            @RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
            @RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
            @RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
            @RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
            @RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
            @RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta,
            @RequestParam(name = "limite", defaultValue = "5", required = false) Integer limite);

    /**
     * GET /dashboard/graficos : Obtener datos de gráficos para el dashboard
     */
    @GetMapping("/graficos")
    ResponseEntity<GlobalResponse> consultarGraficos(
            @RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
            @RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
            @RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
            @RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
            @RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
            @RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
            @RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
            @RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);
}
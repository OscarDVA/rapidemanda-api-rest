package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RequestMapping(value = "catalogos", produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public interface GestionCatalogo extends Base {

    @GetMapping
    ResponseEntity<GlobalResponse> consultarCatalogosJerarquicos(
            @RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
            @RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
            @RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
            @RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
            @RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
            @RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
            @RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
            @RequestParam(name = "formatoRespuesta", defaultValue = "json", required = false) String formatoRespuesta);
}
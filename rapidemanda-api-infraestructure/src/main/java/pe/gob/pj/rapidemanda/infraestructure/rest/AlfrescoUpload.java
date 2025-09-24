package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RequestMapping(value = "alfresco", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
public interface AlfrescoUpload extends Base{

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<GlobalResponse> upload(
        @RequestAttribute(name = ProjectConstants.AUD_CUO, required = false) String cuo,
        @RequestPart("file") MultipartFile file,
        @RequestParam("path") String path
    );

    @GetMapping(value = "/obtener")
    ResponseEntity<byte[]> obtenerDocumento(
        @RequestAttribute(name = ProjectConstants.AUD_CUO, required = false) String cuo,
        @RequestParam("id") String id
    );
}

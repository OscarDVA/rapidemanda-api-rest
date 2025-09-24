package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.InputStream;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDocumentoUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlfrescoUploadController implements AlfrescoUpload, Serializable{

	private static final long serialVersionUID = 1L;

    @Qualifier("gestionDocumentoUseCasePort")
    private final GestionDocumentoUseCasePort gestionDocumentoUseCasePort;

    @Override
    public ResponseEntity<GlobalResponse> upload(String cuo, MultipartFile file, String path) {

        GlobalResponse res = new GlobalResponse();
        res.setCodigoOperacion(cuo);

        try {
            InputStream inputStream = file.getInputStream();
            String uuid = gestionDocumentoUseCasePort.subirDocumentoAlfresco(
                    cuo,
                    path,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    inputStream);
            res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
            res.setDescripcion("Archivo subido correctamente");
            res.setData(uuid);
        } catch (Exception e) {
            res.setCodigo(Errors.ERROR_ALFRESCO_SUBIR_ARCHIVO.getCodigo());
            res.setDescripcion("Error al subir documento al Alfresco: " + e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> obtenerDocumento(String cuo, String id) {
        try {
            byte[] contenido = gestionDocumentoUseCasePort.descargarDocumento(cuo, id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("documento-descargado.pdf")
                    .build());

            return new ResponseEntity<>(contenido, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
}

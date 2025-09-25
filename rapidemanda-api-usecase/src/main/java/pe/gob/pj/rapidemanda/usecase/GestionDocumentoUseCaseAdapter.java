package pe.gob.pj.rapidemanda.usecase;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.port.files.CmisPort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDocumentoUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.file.CMISFileProperties;

@Slf4j
@Service("gestionDocumentoUseCasePort")
public class GestionDocumentoUseCaseAdapter implements GestionDocumentoUseCasePort{
	
	   private final CmisPort cmisPort;
	   
	    public GestionDocumentoUseCaseAdapter(@Qualifier("cmisPort") CmisPort cmisPort) {
	        this.cmisPort = cmisPort;
	    }
	   
	   @Override
    public String subirDocumento(String cuo, InputStream inputStream) throws Exception {
        try {
            log.info("{} Iniciando subida de documento a Alfresco", cuo);
            
            CMISFileProperties props = new CMISFileProperties();
            props.addProp("cmis:objectTypeId", "cmis:document");
            props.addProp("cmis:name", "documento.pdf");

            String documentId = cmisPort.cmisUploadFile(cuo, props, inputStream, "/RAPIDEMANDA", "documento.pdf", "application/pdf");
            
            log.info("{} Documento subido exitosamente con ID: {}", cuo, documentId);
            return documentId;
            
        } catch (Exception e) {
            log.error("{} Error al subir documento a Alfresco: {}", cuo, e.getMessage(), e);
            throw new Exception("Error al subir documento a Alfresco: " + e.getMessage(), e);
        }
    }

	    @Override
    public String subirDocumentoAlfresco(String cuo, String folderPath, String fileName, String mimeType,
            InputStream inputStream) throws Exception {
        try {
            log.info("{} Iniciando subida de documento '{}' a Alfresco en ruta: {}", cuo, fileName, folderPath);
            
            // Crear las carpetas indicadas si no existen
            String destPath = "/RAPIDEMANDA";
            if (folderPath != null && !folderPath.trim().isEmpty()) {
                String[] folders = folderPath.split("/");
                for (String folder : folders) {
                    if (folder != null && !folder.trim().isEmpty()) {
                        log.debug("{} Creando carpeta si no existe: {}/{}", cuo, destPath, folder);
                        cmisPort.createFolferIfNotExist(destPath, folder);
                        destPath = destPath + "/" + folder;
                    }
                }
            }

            CMISFileProperties props = new CMISFileProperties();
            props.addProp("cmis:objectTypeId", "cmis:document");
            props.addProp("cmis:name", fileName);

            String documentId = cmisPort.cmisUploadFile(cuo, props, inputStream, destPath, fileName, mimeType);
            
            log.info("{} Documento '{}' subido exitosamente con ID: {} en ruta: {}", cuo, fileName, documentId, destPath);
            return documentId;
            
        } catch (Exception e) {
            log.error("{} Error al subir documento '{}' a Alfresco: {}", cuo, fileName, e.getMessage(), e);
            throw new Exception("Error al subir documento '" + fileName + "' a Alfresco: " + e.getMessage(), e);
        }
    }
	    
	    @Override
    public byte[] descargarDocumento(String cuo, String uuid) throws Exception {
        try {
            log.info("{} Iniciando descarga de documento con ID: {}", cuo, uuid);
            
            byte[] documentBytes = cmisPort.getFileByUuid(uuid);
            
            log.info("{} Documento descargado exitosamente con ID: {}", cuo, uuid);
            return documentBytes;
            
        } catch (Exception e) {
            log.error("{} Error al descargar documento con ID '{}': {}", cuo, uuid, e.getMessage(), e);
            throw new Exception("Error al descargar documento con ID '" + uuid + "': " + e.getMessage(), e);
        }
    }

}

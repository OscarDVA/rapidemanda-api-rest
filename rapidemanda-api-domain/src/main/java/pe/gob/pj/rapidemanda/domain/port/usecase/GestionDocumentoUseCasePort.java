package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.io.InputStream;

public interface GestionDocumentoUseCasePort {
	  /**
     * Sube un archivo a Alfresco
     * 
     * @param cuo Código único de operación
     * @param request Datos del documento
     * @param inputStream Flujo del archivo
     * @return UUID del documento almacenado
     * @throws Exception
     */
    String subirDocumento(String cuo, InputStream inputStream) throws Exception;

    /**
     * Sube un archivo a una ruta específica en Alfresco creando las carpetas
     * intermedias si no existen.
     *
     * @param cuo         Código único de operación
     * @param folderPath  Ruta de carpetas donde se almacenará el archivo. Puede
     *                    estar compuesta por varios niveles separados por '/'
     * @param fileName    Nombre con el que se guardará el archivo
     * @param mimeType    Tipo de contenido del archivo
     * @param inputStream Flujo del archivo a almacenar
     * @return UUID del documento almacenado
     * @throws Exception en caso ocurra algún error con Alfresco
     */
    String subirDocumentoAlfresco(String cuo, String folderPath, String fileName, String mimeType,
                          InputStream inputStream) throws Exception;

    byte[] descargarDocumento(String cuo, String uuid) throws Exception;
}

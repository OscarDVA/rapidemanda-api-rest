package pe.gob.pj.rapidemanda.domain.port.files;

import org.apache.chemistry.opencmis.client.api.Session;

import pe.gob.pj.rapidemanda.domain.utils.file.CMISFileProperties;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public interface CmisPort {
	
	/**
	 * Método que sirve para inicializar las variables de conexión a alfresco
	 * @param host Ip de alfresco al cual se quiere conectar
	 * @param puerto Puerto de alfresco al cual se quiere conectar
	 * @param usuario Usuario con el que se quiere conectar a alfresco
	 * @param clave Clave de usuario con el que se quiere conectar a alfresco
	 * @param path Carpeta raiz a cual se quiere conectar
	 * @param version Versión de alfresco al cual se quiere conectar
	 * @throws Exception
	 */
	public void inicializarCredenciales(String host, String puerto, String usuario, String clave, String path, String version) throws Exception;

    /**
     *
     * @param path Root path where create the folder
     * @param newFolderName Folder name of new folder
     * @return
     */
    public String cmisCreateFolder(String path, String newFolderName) throws Exception;

    public String cmisCreateFolder( String newFolderName) throws Exception;

    /**
     * Comprueba la existencia de una carpeta
     * @param path
     * @return
     */
    public Boolean cmisExistsFolder(String path) throws Exception;

    /***
     *
     * @param fp propiedades del documento como FileProperties
     * @param sourceFile Contenido del archivo como ByteArrayInputStream
     * @param fileName Nombre de archivo que tendra en Alfresco
     * @param destPath ruta de la carpeta donde se grabara el documento en el alfresco
     * @param mimetype MimeType del archivo
     * @param fileSize tama�o del archivo
     * @return retorna el uuid del documento almacenado en forma de String
     */
    public String  cmisUploadFile( CMISFileProperties fp, ByteArrayInputStream sourceFile, String fileName, String destPath, String mimetype, long fileSize) throws Exception;

    /**
     * Método que sirve para subir archivo al alfresco desde un flujo de datos
     * @param cuo			Código único de operación
     * @param fp			Propiedades del documento
     * @param inputFile		Flujo de stream del documento a subir
     * @param path			Ruta de la carpeta donde se grabara el documento en el alfresco
     * @param nameFile		Nombre del archivo que tendra en alfresco
     * @param mimetype 		MimeType del archivo
     * @return				Uuid del documento almacenado en forma de String
     * @throws Exception
     */
    public String cmisUploadFile (String cuo, CMISFileProperties fp, InputStream inputFile, String path, String nameFile, String mimetype) throws Exception;
    
    /***
     *
     * @param fp propiedades del documento como FileProperties
     * @param sourceFile Contenido del archivo como byte array (hacer un getBytes(Charset) .getBytes("UTF-8")
     * @param fileName Nombre de archivo que tendra en Alfresco
     * @param destPath ruta de la carpeta donde se grabara el documento en el alfresco
     * @param mimetype MimeType del archivo
     * @return retorna el uuid del documento almacenado en forma de String
     */
    public String cmisUploadFile(CMISFileProperties fp, byte[] sourceFile, String fileName, String destPath, String mimetype) throws Exception;


    /***
     *
     * @param fp propiedades del documento como FileProperties
     * @param sourceFile Archivo a subir, ruta completa con nombre de archivo
     * @param fileName Nombre de archivo que tendra en Alfresco
     * @param destPath ruta de la carpeta donde se grabara el documento en el alfresco
     * @param mimetype MimeType del archivo
     * @return retorna el uuid del documento almacenado en forma de String
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String  cmisUploadFile(CMISFileProperties fp, String sourceFile, String fileName, String destPath, String mimetype) throws Exception ;

    /***
     *
     * @param fileName es el nombre del archivo
     * @param fileDest es la ruta donde
     * @return
     * @throws IOException
     */
    public String cmisGetFile(String fileName, String fileDest) throws Exception;

    /***
     *
     * @param fileName
     * @param fileDest
     * @param exist
     * @param exist1
     * @return
     * @throws IOException
     */
    public String cmisGetFile(String fileName, String fileDest,boolean exist,boolean exist1) throws Exception;

    public ArrayList<String> cmisGetFolderContents(String subFolder) throws Exception;

    public Boolean cmisDeleteFile(String destPath) throws Exception;
    public Boolean cmisDeleteFile(String destPath, String name) throws Exception;

    public Boolean cmisExistFile(String destPath, String name) throws Exception;

    public Object cmisFindDocument(String destPath, String name) throws Exception;

    public String cmisUpdateDocument(CMISFileProperties fp, String sourceFile, String fileName, String destPath, String mimetype)  throws Exception;

    public String getFilePath(String docId) throws Exception;

    /**
     * Método para buscar documento por uuid en alfresco
     * 
     * @param docId identificador uuid del documento 
     * @return retorna el documento en arreglo de bytes o nulo si no lo encuentra o no tiene contenido
     * @throws Exception
     */
    public byte[] getFileByUuid(String docId)  throws Exception;

    public Map<String, Object> executeQuery(String query) throws Exception;

    /**
     * Método para verificar si existe carpeta y crear en caso de no existir
     * 
     * @param path ruta raiz donde se verificara la carpeta
     * @param newNameFolder carpeta que se verificara y/o creara
     * @throws Exception
     */
    public void createFolferIfNotExist(String path, String newNameFolder) throws Exception;

	/**
	 * Metodo para abrir una conexión al repositorio Alfresco.
	 */
	public Session openSession();
	/**
	 * Metodo para cerrar una conexión al repositorio Alfresco.
	 */
	public void finalizeSession();
	
}

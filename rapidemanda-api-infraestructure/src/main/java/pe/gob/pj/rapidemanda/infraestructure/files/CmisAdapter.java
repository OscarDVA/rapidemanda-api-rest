package pe.gob.pj.rapidemanda.infraestructure.files;

import org.apache.chemistry.opencmis.client.api.*;

import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.port.files.CmisPort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;
import pe.gob.pj.rapidemanda.domain.utils.file.CMISFileProperties;

import org.apache.chemistry.opencmis.client.api.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

@Slf4j
@Component("cmisPort")
public class CmisAdapter implements CmisPort {

	private String atompubUrl41 = "alfresco/cmisatom";
	private String atompubUrl42 = "alfresco/api/-default-/public/cmis/versions/1.0/atom";
	private String rootFolder = "default";
	
    private Session session;
    private final Object sessionLock = new Object();
    private volatile long sessionCreationTime = 0;
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutos
    
    private final Map<String, String> credencialesConexion = new ConcurrentHashMap<>();
    private String cuo = "default";
    private volatile boolean credencialesInicializadas = false;

    /**
     * Inicializa automáticamente las credenciales de Alfresco al crear el bean
     */
    @PostConstruct
    private void autoInicializarCredenciales() {
        try {
            log.info("Inicializando credenciales de Alfresco automáticamente...");
            inicializarCredenciales(
                ProjectProperties.getAlfrescoHost(),
                ProjectProperties.getAlfrescoPuerto(),
                ProjectProperties.getAlfrescoUsuario(),
                ProjectProperties.getAlfrescoClave(),
                ProjectProperties.getAlfrescoPath(),
                ProjectProperties.getAlfrescoVersion()
            );
            log.info("Credenciales de Alfresco inicializadas correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar credenciales de Alfresco automáticamente: {}", e.getMessage());
            // No lanzamos la excepción para permitir que el bean se cree
            // Las credenciales se pueden inicializar manualmente si es necesario
        }
    }

    /**
     * Verifica si las credenciales ya están inicializadas
     */
    public boolean isCredencialesInicializadas() {
        return credencialesInicializadas && !credencialesConexion.isEmpty();
    }

    /**
     * Asegura que las credenciales estén inicializadas antes de realizar operaciones
     */
    private void asegurarCredencialesInicializadas() throws Exception {
        if (!isCredencialesInicializadas()) {
            log.warn("Credenciales no inicializadas, intentando inicialización automática...");
            autoInicializarCredenciales();
            if (!isCredencialesInicializadas()) {
                throw new Exception("Las credenciales de Alfresco no están inicializadas y no se pudieron inicializar automáticamente");
            }
        }
    }

    /**
     * Verifica si la sesión actual está activa y no ha expirado
     */
    private boolean isSessionActive() {
        if (session == null) {
            return false;
        }
        
        // Verificar timeout
        long currentTime = System.currentTimeMillis();
        if (sessionCreationTime > 0 && (currentTime - sessionCreationTime) > SESSION_TIMEOUT_MS) {
            log.warn("Sesión CMIS ha expirado después de {} ms", SESSION_TIMEOUT_MS);
            return false;
        }
        
        // Verificar si la sesión sigue siendo válida
        try {
            session.getRepositoryInfo(); // Test básico de conectividad
            return true;
        } catch (Exception e) {
            log.warn("Sesión CMIS no está activa: {}", e.getMessage());
            return false;
        }
    }

	@Override
	public void inicializarCredenciales(String host, String puerto, String usuario, String clave, String path,
			String version) throws Exception {
		String baseUrl = "http://" + host + ":" + puerto;
    	credencialesConexion.put(SessionParameter.ATOMPUB_URL, baseUrl +"/" + (version.equals(ProjectConstants.Alfresco.VERSION_4_2)?this.atompubUrl42 : this.atompubUrl41));
    	credencialesConexion.put(SessionParameter.USER, usuario);
    	credencialesConexion.put(SessionParameter.PASSWORD, clave);
    	credencialesConexion.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
    	// Set the alfresco object manager
        // Used when using the CMIS extension for Alfresco for working with aspects
        //parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
    	// Forzar URLs públicas en todas las operaciones
        credencialesConexion.put("org.apache.chemistry.opencmis.binding.atompub.url.override", baseUrl);
        credencialesConexion.put("org.apache.chemistry.opencmis.binding.spi.type", "atompub");
        credencialesConexion.put("org.apache.chemistry.opencmis.server.override", "true");
        credencialesConexion.put("org.apache.chemistry.opencmis.server.value", baseUrl);
    	this.rootFolder = path;
    	this.credencialesInicializadas = true;
    	log.debug("Credenciales CMIS inicializadas para host: {}, puerto: {}, usuario: {}", host, puerto, usuario);
	}

    @Override
    public String cmisCreateFolder(String path, String newFolderName) throws Exception {
        Session session = null;
        Folder rootOperationFolder = null;
        CmisObject object = null;
        Folder newFolder = null;

        try{
            session = openSession();
            object = session.getObjectByPath(path);

            if (object!=null){
                rootOperationFolder = (Folder) object;
            }
            //crear carpeta
            //System.out.println("Creating '"+newFolderName+"' in the root folder");
            Map<String, String> newFolderProps = new HashMap<String, String>();
            newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
            newFolderProps.put(PropertyIds.NAME, newFolderName);

            newFolder = rootOperationFolder.createFolder(newFolderProps);
            return newFolder.getId();
        }catch (CmisContentAlreadyExistsException cmiException){
        	//Folder ya existe, no lanzamos la excepción.
        	return null;
        }catch (Exception ex){
            throw ex;
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }


    }

    @Override
    public String cmisCreateFolder(String newFolderName) throws Exception{
        Session session = null;
        Folder rootOperationFolder = null;
        Folder newFolder = null;

        try{
            session = openSession();
            rootOperationFolder = getRootFolder(session);

            //crear carpeta
            System.out.println("Creating '"+newFolderName+"' in the root folder");
            Map<String, String> newFolderProps = new HashMap<String, String>();
            newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
            newFolderProps.put(PropertyIds.NAME, newFolderName);

            newFolder = rootOperationFolder.createFolder(newFolderProps);

        }catch (Exception ex){
            throw new Exception(ex.getMessage(), ex.getCause());
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }

        return newFolder.getId();
    }

    private Folder getRootFolder(Session session) throws Exception {

        try{
            CmisObject object = session.getObjectByPath("/" + this.rootFolder);
            Folder rootOperationFolder  = (Folder) object;
            return rootOperationFolder;
        }catch (Exception ex){
            throw new Exception(ex.getMessage(), ex.getCause());
        }finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }
    private Folder getFolder(String path, Session session) throws Exception{
        try{
            CmisObject object = session.getObjectByPath(path);
            
            Folder rootOperationFolder  = (Folder) object;
            return rootOperationFolder;
        }catch (Exception ex){
            throw ex;
        }finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }

    /**
     * Comprueba la existencia de una carpeta
     * @param path Path que se quiere verificar su existencia, debe iniciar con /
     * @return
     */
    public Boolean cmisExistsFolder(String path)throws Exception {
        Session session = null;
        Boolean exist = new Boolean(false);
        try{
            session = openSession();
            CmisObject object = session.getObjectByPath(path);
            if (object!=null && ((Folder) object).getPath().equals(path)){
                exist = true;
            }
        }catch(CmisObjectNotFoundException e ){
            return false;
        }catch (Exception e){
            throw  e;
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
        return exist;
    }




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
    public String  cmisUploadFile( CMISFileProperties fp, ByteArrayInputStream sourceFile, String fileName, String destPath, String mimetype, long fileSize) throws Exception {

        Session session = null;
        Document doc = null;
        Folder folder;
        ContentStream contentStream;

        try {
            session = openSession();
            contentStream = session.getObjectFactory().createContentStream(fileName, fileSize, mimetype, sourceFile);
            folder = this.getFolder(destPath, session);
            doc = folder.createDocument(fp.getProperties(), contentStream, VersioningState.MAJOR);
            return doc.getId();
        }catch (Exception e){
            throw e;
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }

	@Override
	public String cmisUploadFile(String cuo, CMISFileProperties fp, InputStream inputFile, String path, String nameFile, String mimetype)
			throws Exception {
		Session session = null;
        Document doc = null;
        Folder folder;
        ContentStream contentStream;
        try {
            session = openSession();
            folder = (Folder) session.getObjectByPath(path);
            
            // Generar un nombre único si el archivo ya existe
            String uniqueFileName = nameFile;
            boolean fileExists = true;
            int counter = 1;
            
            while (fileExists) {
                try {
                    String filePath = path + "/" + uniqueFileName;
                    session.getObjectByPath(filePath);
                    
                    // Si llegamos aquí, el archivo existe, generamos un nuevo nombre
                    String baseName = getBaseName(nameFile);
                    String extension = getExtension(nameFile);
                    uniqueFileName = baseName + "_" + counter + (extension.isEmpty() ? "" : "." + extension);
                    counter++;
                } catch (CmisObjectNotFoundException e) {
                    // El archivo no existe, salimos del bucle
                    fileExists = false;
                }
            }
            
            // Actualizar el nombre del archivo en las propiedades
            fp.addProp("cmis:name", uniqueFileName);
            
            contentStream = session.getObjectFactory().createContentStream(uniqueFileName, -1, mimetype, inputFile);
            doc = folder.createDocument(fp.getProperties(), contentStream, VersioningState.MAJOR);
            return doc.getId();
        } catch (Exception e) {
            throw e;
        } finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
	}
	
	/**
     * Obtiene el nombre base de un archivo sin la extensión
     * @param fileName Nombre del archivo
     * @return Nombre base sin extensión
     */
    private String getBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
    
    /**
     * Obtiene la extensión de un archivo
     * @param fileName Nombre del archivo
     * @return Extensión del archivo sin el punto
     */
    private String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /***
     *
     * @param fp propiedades del documento como FileProperties
     * @param sourceFile Contenido del archivo como byte array (hacer un getBytes(Charset) .getBytes("UTF-8")
     * @param fileName Nombre de archivo que tendra en Alfresco
     * @param destPath ruta de la carpeta donde se grabara el documento en el alfresco
     * @param mimetype MimeType del archivo
     * @return retorna el uuid del documento almacenado en forma de String
     */
    public String cmisUploadFile(CMISFileProperties fp, byte[] sourceFile, String fileName, String destPath, String mimetype)throws Exception{

        ByteArrayInputStream input = new ByteArrayInputStream(sourceFile);
        return cmisUploadFile(fp, input, fileName, destPath, mimetype, sourceFile.length);
    }



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
    public String  cmisUploadFile(CMISFileProperties fp, String sourceFile, String fileName, String destPath, String mimetype) throws Exception {

        try {
            File file = new File(sourceFile);
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = null;
            dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            return cmisUploadFile(fp, fileData, fileName, destPath, mimetype);
        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage(),e.getCause());
        } catch (IOException e) {
            throw new Exception(e.getMessage(),e.getCause());
        } catch (Exception e){
            throw new Exception(e.getMessage(),e.getCause());
        }
    }


    /***
     *
     * @param fileName es el nombre del archivo
     * @param fileDest es la ruta donde
     * @return
     * @throws IOException
     */
    public String cmisGetFile(String fileName, String fileDest) throws Exception{
        Session session = null;
        try{
            session = openSession();
            //leer el doc por su path
            //String path = "/EJD" + "/" + fileName;
            String path = fileName;
            System.out.println("Getting object by path " + path);

            Document doc = (Document) session.getObjectByPath(path);
            ContentStream contentStream = doc.getContentStream();
            if (contentStream != null) {

                //(contentStream.getStream());
                // write the inputStream to a FileOutputStream
                InputStream inputStream = contentStream.getStream();
                OutputStream outputStream =
                        new FileOutputStream(new File(fileDest));

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                outputStream.close();
            } else {
                System.out.println("No content.");
            }

           return fileDest;

        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }


    }

    /***
     *
     * @param fileName
     * @param fileDest
     * @param exist
     * @param exist1
     * @return
     * @throws IOException
     */
    public String cmisGetFile(String fileName, String fileDest,boolean exist,boolean exist1) throws Exception{

        Session session = null;

        try{
            session = openSession();
            //leer el doc por su path
            //String path = CMISConfiguracion.SITE_ROOT_FOLDER_PATH_PLANTILLAS + "/" + fileName;
            System.out.println("Getting object by path " + fileName);

            Document doc = (Document) session.getObjectByPath(fileName);
            ContentStream contentStream = doc.getContentStream();
            if (contentStream != null) {

                //(contentStream.getStream());
                // write the inputStream to a FileOutputStream
                InputStream inputStream = contentStream.getStream();
                OutputStream outputStream =
                        new FileOutputStream(new File(fileDest));

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                inputStream.close();
                outputStream.close();
            } else {
                System.out.println("No content.");
            }

            return fileDest;

        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }
        finally {
            if (null != session){session.clear();}
            finalizeSession();
        }

    }

    public ArrayList<String> cmisGetFolderContents(String subFolder)throws Exception{
        ArrayList<String> listado = new ArrayList<String>();
        Session session = null;

        try{
            session = openSession();
            Folder folder  = (Folder) getFolder("/EJD" + "/" + subFolder, session);
            ItemIterable<CmisObject> children = folder.getChildren();

            for (CmisObject o : children) {
                listado.add(o.getName());
            }

            return listado;

        }catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }
        finally {
            if (null != session){session.clear();}
            finalizeSession();
        }

    }

    @Override
    public Boolean cmisDeleteFile(String destPath) throws Exception {
        Session session = null;
        Boolean delete = false;
        try {

            session = openSession();
            CmisObject object = session.getObjectByPath(destPath);
            if (object != null) {
                session.delete(object);
                delete = true;
            }

            return delete;

        }catch (CmisObjectNotFoundException e) {
            throw new Exception(e.getMessage(),e.getCause());
        }catch (Exception e){
            throw new Exception(e.getMessage(),e.getCause());
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }

    public Boolean cmisDeleteFile(String destPath, String name) throws Exception {
        Session session = null;
        Boolean delete = false;
        try {

            session = openSession();
            CmisObject object = session.getObjectByPath(destPath + "/" + name);
            if (object != null) {
                session.delete(object);
                delete = true;
            }
            return delete;
        } catch (CmisObjectNotFoundException e) {
            throw new Exception(e.getMessage(),e.getCause());
        }catch (Exception e){
            throw new Exception(e.getMessage(),e.getCause());
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }

    public Boolean cmisExistFile(String destPath, String name) throws Exception {
        Session session = null;
        Boolean exist = false;
        try {
            session = openSession();
            CmisObject object = session.getObjectByPath(destPath + "/" + name);
            if (object != null) {
                exist = true;
            }

            return exist;

        } catch (CmisObjectNotFoundException e) {
            throw new Exception(e.getMessage(),e.getCause());
        }catch (Exception e){
            throw new Exception(e.getMessage(),e.getCause());
        }
        finally {
            if(null != session){session.clear();}
            finalizeSession();
        }
    }

    public Object cmisFindDocument(String destPath, String name) throws Exception{
        Session session = null;
        CmisObject object = null;

        try {
            session = openSession();
            object = session.getObjectByPath(destPath + "/" + name);
            if (object != null) {
                return object;
            }
        } catch (CmisObjectNotFoundException e) {
            return false;
        } catch (Exception e){
            throw new Exception(e.getMessage(),e.getCause());
        }
        finally {
            if (null != session ){session.clear();}
            finalizeSession();
        }
        return object;
    }

    public String cmisUpdateDocument(CMISFileProperties fp, String sourceFile, String fileName, String destPath, String mimetype) throws Exception{

        try {
            File file = new File(sourceFile);
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            return cmisUpdateDocument(fp, fileData, fileName, destPath, mimetype);
        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }
    }

    private String cmisUpdateDocument(CMISFileProperties fp, byte[] sourceFile,String fileName, String destPath,String mimetype) throws Exception{
        ByteArrayInputStream input = new ByteArrayInputStream(sourceFile);
        return cmisUpdateDocument(fp, input, fileName, destPath, mimetype, sourceFile.length);
    }

    private String cmisUpdateDocument(CMISFileProperties fp, ByteArrayInputStream sourceFile, String fileName, String destPath, String mimetype, long fileSize)throws Exception{
        Session session = null;
        CmisObject doc = null;

        try {
            session = openSession();
            //obtenermos el documento en alfresco
            doc = session.getObjectByPath(destPath + "/" + fileName);


            if(doc == null){
                throw new Exception(" No existe documento ");
            }

            if (((DocumentType)(doc.getType())).isVersionable() && doc.getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_OUT)) {
                doc.refresh();
                org.alfresco.cmis.client.impl.AlfrescoDocumentImpl newObject = ((org.alfresco.cmis.client.impl.AlfrescoDocumentImpl)doc);
                ObjectId  idOfCheckedOutDocument = newObject.checkOut();
                Document pwc = (Document) session.getObject(idOfCheckedOutDocument);

                ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, fileSize, mimetype, sourceFile);
                try {
                    ObjectId objectId = pwc.checkIn(false, null, contentStream," Mayor version ");
                    doc = (Document) session.getObject(objectId);
                } catch (CmisBaseException e) {
                    e.printStackTrace();
                    pwc.cancelCheckOut();
                }
                List<Document> versions = newObject.getAllVersions();

                for (Document version : versions) {
                    System.out.println("\tname: " + version.getName());
                    System.out.println("\tversion label: " + version.getVersionLabel());
                    System.out.println("\tversion series id: " + version.getVersionSeriesId());
                    System.out.println("\tchecked out by: "
                            + version.getVersionSeriesCheckedOutBy());
                    System.out.println("\tchecked out id: "
                            + version.getVersionSeriesCheckedOutId());
                    System.out.println("\tmajor version: " + version.isMajorVersion());
                    System.out.println("\tlatest version: " + version.isLatestVersion());
                    System.out.println("\tlatest major version: " + version.isLatestMajorVersion());
                    System.out.println("\tcheckin comment: " + version.getCheckinComment());
                    System.out.println("\tcontent length: " + version.getContentStreamLength()
                            + "\n");
                }
            }

            return doc.getId();

        } catch (CmisObjectNotFoundException e) {
            throw new Exception(e.getMessage(), e.getCause());
        }  catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }finally {
            if (null != session){session.clear();}
            finalizeSession();
        }
    }


    public String getFilePath(String docId) throws Exception{
        Session session = null;

        try {
            session = openSession();
            CmisObject object = session.getObject(docId);
            if (object == null) {
                return "";
            }
            return ((Document)object).getPaths().get(0);
        } catch (CmisObjectNotFoundException e) {
            System.out.println(e.getMessage());
            return "";
        }catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }
        finally {
            if (null != session){session.clear();}
            finalizeSession();
        }
    }


    public byte[] getFileByUuid(String docId) throws Exception {

        Session session = null;
        byte[] respuesta=null;
        try {
            session = openSession();
            CmisObject object = session.getObject(docId);
            if(object!=null) {
            	Document doc = (Document) object;
                ContentStream contentStream = doc.getContentStream();
                if (contentStream != null) {
                    respuesta=inputStreamToBytes(contentStream.getStream());
                    log.info("{} Se descargo de manera correcta de alfresco({}) el documento: {}",cuo,docId,doc.getName());
                } else {
                    log.warn("{} El documento {} descargado de alfresco({}) no tiene contenido.",cuo,doc.getName(),docId);
                }
            }
        } catch (CmisObjectNotFoundException e) {
        	log.error("{} No se encontro en alfresco el objeto: {} (CmisObjectNotFoundException : {})",cuo,docId,e);
        } catch (Exception e){
        	log.error("{} Error al obtener de alfresco el objeto con uuid: {} (Exception : {})",cuo,docId,e);
        } finally{
            if (null != session){session.clear();}
            finalizeSession();
        }
        return respuesta;

    }

    public static byte[] inputStreamToBytes(InputStream in) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        byte[] bytes = new byte[512];
        int readBytes;
        try {

            while ((readBytes = in.read(bytes)) > 0) {
                outputStream.write(bytes, 0, readBytes);
            }

            byte[] byteData = outputStream.toByteArray();
            outputStream.close();
            return byteData;

        } catch (IOException e) {
            throw new Exception(e.getMessage(), e.getCause());
        } catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }

    }


    public Map<String, Object> executeQuery(String query)throws Exception{
        Map<String, Object> mapProperties = new HashMap<>();
        Session session = null;
        try{

            session = openSession();
            ItemIterable<QueryResult> results = session.query(query, false);

            for(QueryResult hit: results) {
                for(PropertyData<?> property: hit.getProperties()){
                    String queryName = property.getQueryName();
                    Object value = property.getFirstValue();
                    if(null != value){
                        mapProperties.put(queryName, value);
                    }
                }
            }

            if(mapProperties.isEmpty()){
                mapProperties = null;
            }

            return mapProperties;
        }catch (Exception e){
            throw new Exception(e.getMessage(), e.getCause());
        }finally{
            if (null != session){session.clear();}
            finalizeSession();
        }

    }


    public void createFolferIfNotExist(String path, String newNameFolder) throws Exception{
        String fullPath;

        if("/".equals(path)){
            fullPath = path + newNameFolder;

        }else{
            fullPath = path + "/" + newNameFolder;
        }

        Boolean existFolder =  cmisExistsFolder(fullPath);

        if(existFolder.equals(false)){
            /*** Creamos el folder ***/
            cmisCreateFolder(path,newNameFolder);
            //System.out.println("Folder " + fullPath + " creado con exito.");
        }

    }

//    public Map<String, String> setting(Repository repositoriy){
//        Map<String, String> parameter = null;
//
//        if("4.1".equals(repositoriy.getAtompub_url())){
//            repositoriy.setAtompub_url(Repository.ATOMPUB_URL_4_1);
//        }else {
//        	repositoriy.setAtompub_url(Repository.ATOMPUB_URL_4_2);
//        }
//
//        parameter = new HashMap<String, String>();
//        //parameter.put(SessionParameter.REPOSITORY_ID, "");
//        parameter.put(SessionParameter.USER, repositoriy.getUser());
//        parameter.put(SessionParameter.PASSWORD, repositoriy.getPassword());
//        parameter.put(SessionParameter.ATOMPUB_URL, "http://"+ repositoriy.getHost() +":"+ repositoriy.getPort() +"/" + repositoriy.getAtompub_url());
//        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
//        // Set the alfresco object manager
//        // Used when using the CMIS extension for Alfresco for working with aspects
//        //parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
//        
//        return parameter;
//    }

    public Session openSession() {
        try {
            asegurarCredencialesInicializadas();
            
            // Sincronización thread-safe para la creación de sesión
            synchronized (sessionLock) {
                // Verificar si necesitamos crear una nueva sesión
                if (!isSessionActive()) {
                    if (session != null) {
                        log.debug("Sesión CMIS inactiva o expirada, creando nueva sesión...");
                        try {
                            session.clear();
                        } catch (Exception e) {
                            log.debug("Error al limpiar sesión anterior: {}", e.getMessage());
                        }
                        session = null;
                    }
                    
                    log.debug("Creando nueva sesión CMIS...");
                    Map<String, String> parameter = this.credencialesConexion;
                    SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
                    List<Repository> repositories = sessionFactory.getRepositories(parameter);
                    session = repositories.get(0).createSession();
                    sessionCreationTime = System.currentTimeMillis();
                    log.debug("Sesión CMIS creada exitosamente");
                }
            }
            return session;
        } catch (Exception e) {
            log.error("Error al abrir sesión CMIS: {}", e.getMessage());
            throw new RuntimeException("Error al abrir sesión CMIS: " + e.getMessage(), e);
        }
    }

    public void finalizeSession(){
        synchronized (sessionLock) {
            if(null != this.session){
                log.debug("Finalizando sesión CMIS...");
                this.session.clear();
                this.session = null;
                this.sessionCreationTime = 0;
                log.debug("Sesión CMIS finalizada exitosamente");
            }
        }
    }


}

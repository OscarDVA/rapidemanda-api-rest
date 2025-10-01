package pe.gob.pj.rapidemanda.domain.port.persistence;

public interface GestionReportePdfPersistencePort {

	/**
	 * Método que permite generar un reporte PDF de una demanda
	 * 
	 * @param cuo Código único de operación
	 * @param idDemanda Identificador de la demanda
	 * @return Array de bytes del PDF generado
	 * @throws Exception Si ocurre algún error durante la generación
	 */
	public byte[] generarReporteDemanda(String cuo, Integer idDemanda) throws Exception;
	
	/**
	 * Método que permite obtener una imagen desde Alfresco por su ID
	 * 
	 * @param cuo Código único de operación
	 * @param archivoId Identificador del archivo en Alfresco
	 * @return Array de bytes de la imagen
	 * @throws Exception Si ocurre algún error al obtener la imagen
	 */
	public byte[] obtenerImagenAlfresco(String cuo, String archivoId) throws Exception;
}
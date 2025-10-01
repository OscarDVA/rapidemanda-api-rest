package pe.gob.pj.rapidemanda.domain.port.usecase;

public interface GestionReportePdfUseCasePort {

	/**
	 * Método que permite generar un reporte PDF profesional de una demanda
	 * con todas sus relaciones hijas (demandantes, demandados, petitorios, etc.)
	 * 
	 * @param cuo Código único de operación
	 * @param idDemanda Identificador de la demanda para generar el reporte
	 * @return Array de bytes del PDF generado
	 * @throws Exception Si ocurre algún error durante la generación del reporte
	 */
	public byte[] generarReporteDemanda(String cuo, Integer idDemanda) throws Exception;
}
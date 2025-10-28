package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.Map;

public interface GestionReporteExcelPersistencePort {

    /**
     * Genera un archivo Excel multihoja con el maestro de demandas y sus relaciones
     * hijas (demandantes, demandados, petitorios, relación laboral, fundamentaciones y firmas).
     *
     * @param cuo Código único de operación
     * @param filters Filtros para la búsqueda de demandas 
     * @return Bytes del archivo Excel generado (.xlsx)
     * @throws Exception Si ocurre algún error durante la generación
     */
    byte[] generarReporteDemandasExcelMultiHoja(String cuo, Map<String, Object> filters) throws Exception;
}
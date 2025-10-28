package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReporteExcelPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionReporteExcelUseCasePort;

@Slf4j
@Service("gestionReporteExcelUseCasePort")
public class GestionReporteExcelUseCaseAdapter implements GestionReporteExcelUseCasePort {

    private final GestionReporteExcelPersistencePort gestionReporteExcelPersistencePort;

    public GestionReporteExcelUseCaseAdapter(
            @Qualifier("gestionReporteExcelPersistencePort") GestionReporteExcelPersistencePort gestionReporteExcelPersistencePort) {
        this.gestionReporteExcelPersistencePort = gestionReporteExcelPersistencePort;
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
            Exception.class, SQLException.class })
    public byte[] generarReporteDemandasExcelMultiHoja(String cuo, Map<String, Object> filters) throws Exception {
        log.info("Iniciando generación de Excel multihoja de demandas con filtros: {}", filters);

        try {
            byte[] excelBytes = gestionReporteExcelPersistencePort.generarReporteDemandasExcelMultiHoja(cuo, filters);

            if (excelBytes == null || excelBytes.length == 0) {
                throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                        String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_EXPORTAR_EXCEL.getNombre()));
            }

            log.info("Excel multihoja generado exitosamente, tamaño: {} bytes", excelBytes.length);
            return excelBytes;

        } catch (ErrorException e) {
            log.error("Error de negocio al generar Excel multihoja", e);
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al generar Excel multihoja", e);
            throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                    String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_EXPORTAR_EXCEL.getNombre()));
        }
    }
}
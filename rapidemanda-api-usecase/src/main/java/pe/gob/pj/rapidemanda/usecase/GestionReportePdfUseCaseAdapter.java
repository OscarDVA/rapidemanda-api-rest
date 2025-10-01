package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReportePdfPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionReportePdfUseCasePort;

@Slf4j
@Service("gestionReportePdfUseCasePort")
public class GestionReportePdfUseCaseAdapter implements GestionReportePdfUseCasePort {

	private final GestionReportePdfPersistencePort gestionReportePdfPersistencePort;

	public GestionReportePdfUseCaseAdapter(
			@Qualifier("gestionReportePdfPersistencePort") GestionReportePdfPersistencePort gestionReportePdfPersistencePort) {
		this.gestionReportePdfPersistencePort = gestionReportePdfPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public byte[] generarReporteDemanda(String cuo, Integer idDemanda) throws Exception {
		log.info("Iniciando generación de reporte PDF para demanda ID: {}", idDemanda);
		
		try {
			// Validar que el ID de demanda no sea nulo
			if (idDemanda == null || idDemanda <= 0) {
				throw new ErrorException(Errors.DATOS_ENTRADA_INCORRECTOS.getCodigo(),
						String.format(Errors.DATOS_ENTRADA_INCORRECTOS.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()));
			}

			// Generar el reporte PDF
			byte[] pdfBytes = gestionReportePdfPersistencePort.generarReporteDemanda(cuo, idDemanda);
			
			if (pdfBytes == null || pdfBytes.length == 0) {
				throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
						String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()));
			}

			log.info("Reporte PDF generado exitosamente para demanda ID: {}, tamaño: {} bytes", idDemanda, pdfBytes.length);
			return pdfBytes;
			
		} catch (ErrorException e) {
			log.error("Error de negocio al generar reporte PDF para demanda ID: {}", idDemanda, e);
			throw e;
		} catch (Exception e) {
			log.error("Error inesperado al generar reporte PDF para demanda ID: {}", idDemanda, e);
			throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()));
		}
	}
}
package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;
import pe.gob.pj.rapidemanda.domain.port.usecase.AuditoriaGeneralUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionReportePdfUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.mapper.AuditoriaGeneralMapper;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionReportePdfController implements GestionReportePdf, Serializable {
	private static final long serialVersionUID = 1L;

	@Qualifier("gestionReportePdfUseCasePort")
	final GestionReportePdfUseCasePort gestionReportePdfUseCasePort;
	final AuditoriaGeneralUseCasePort auditoriaGeneralUseCasePort;
	final AuditoriaGeneralMapper auditoriaGeneralMapper;

	@Override
	public ResponseEntity<byte[]> generarReporteDemandaPdf(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, Integer idDemanda) {
		
		log.info("{} Iniciando generación de reporte PDF para demanda ID: {}", cuo, idDemanda);
		
		try {
			long inicio = System.currentTimeMillis();
			
			// Generar el reporte PDF
			byte[] pdfBytes = gestionReportePdfUseCasePort.generarReporteDemanda(cuo, idDemanda);
			
			long fin = System.currentTimeMillis();
			
			// Registrar auditoría
			try {
				AuditoriaAplicativos auditoriaAplicativos = auditoriaGeneralMapper.toAuditoriaAplicativos(
						null, // No hay request body para este endpoint
						cuo, ips, usuauth, uri, params, herramienta, 
						Errors.OPERACION_EXITOSA.getCodigo(),
						Errors.OPERACION_EXITOSA.getNombre(), 
						fin - inicio);
				
				// Crear un objeto simple para el cuerpo de la petición
				String jsonString = new ObjectMapper().writeValueAsString(
						java.util.Map.of("idDemanda", idDemanda, "operacion", "generarReportePdf"));
				auditoriaAplicativos.setPeticionBody(jsonString);
				
				auditoriaGeneralUseCasePort.crear(cuo, auditoriaAplicativos);
			} catch (Exception auditException) {
				log.warn("{} Error al registrar auditoría para reporte PDF de demanda ID: {}", 
						cuo, idDemanda, auditException);
				// No fallar la operación por errores de auditoría
			}
			
			// Configurar headers para descarga de PDF
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentLength(pdfBytes.length);
			headers.add(HttpHeaders.CONTENT_DISPOSITION, 
					"attachment; filename=\"reporte_demanda_" + idDemanda + ".pdf\"");
			headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
			headers.add(HttpHeaders.PRAGMA, "no-cache");
			headers.add(HttpHeaders.EXPIRES, "0");
			
			log.info("{} Reporte PDF generado exitosamente para demanda ID: {}, tamaño: {} bytes", 
					cuo, idDemanda, pdfBytes.length);
			
			return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
			
		} catch (ErrorException e) {
			log.error("{} Error de negocio al generar reporte PDF para demanda ID: {}", cuo, idDemanda, e);
			return handlePdfError(cuo, ips, usuauth, uri, params, herramienta, idDemanda, e);
		} catch (Exception e) {
			log.error("{} Error inesperado al generar reporte PDF para demanda ID: {}", cuo, idDemanda, e);
			ErrorException errorException = new ErrorException(
					Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()),
					e.getMessage(), e.getCause());
			return handlePdfError(cuo, ips, usuauth, uri, params, herramienta, idDemanda, errorException);
		}
	}

	/**
	 * Maneja errores en la generación de PDF retornando un PDF con mensaje de error
	 */
	private ResponseEntity<byte[]> handlePdfError(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, Integer idDemanda, ErrorException e) {
		
		try {
			// Registrar auditoría del error
			long tiempoEjecucion = System.currentTimeMillis();
			AuditoriaAplicativos auditoriaAplicativos = auditoriaGeneralMapper.toAuditoriaAplicativos(
					null, cuo, ips, usuauth, uri, params, herramienta, 
					e.getCodigo(), e.getMessage(), tiempoEjecucion);
			
			String jsonString = new ObjectMapper().writeValueAsString(
					java.util.Map.of("idDemanda", idDemanda, "operacion", "generarReportePdf", "error", e.getMessage()));
			auditoriaAplicativos.setPeticionBody(jsonString);
			
			auditoriaGeneralUseCasePort.crear(cuo, auditoriaAplicativos);
		} catch (Exception auditException) {
			log.warn("{} Error al registrar auditoría de error para reporte PDF de demanda ID: {}", 
					cuo, idDemanda, auditException);
		}
		
		// Generar PDF simple con mensaje de error
		byte[] errorPdf = generarPdfError(e.getMessage(), idDemanda);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentLength(errorPdf.length);
		headers.add(HttpHeaders.CONTENT_DISPOSITION, 
				"attachment; filename=\"error_reporte_demanda_" + idDemanda + ".pdf\"");
		
		return new ResponseEntity<>(errorPdf, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Genera un PDF simple con mensaje de error
	 */
	private byte[] generarPdfError(String mensajeError, Integer idDemanda) {
		try {
			// Crear un PDF simple con el mensaje de error usando iText
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
			com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
			com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
			
			document.add(new com.itextpdf.layout.element.Paragraph("ERROR EN GENERACIÓN DE REPORTE")
					.setFontSize(16).setBold());
			document.add(new com.itextpdf.layout.element.Paragraph("Demanda ID: " + idDemanda)
					.setFontSize(12));
			document.add(new com.itextpdf.layout.element.Paragraph("Error: " + mensajeError)
					.setFontSize(10));
			document.add(new com.itextpdf.layout.element.Paragraph("Fecha: " + new java.util.Date())
					.setFontSize(10));
			
			document.close();
			return baos.toByteArray();
		} catch (Exception ex) {
			log.error("Error al generar PDF de error: {}", ex.getMessage());
			// Retornar un PDF mínimo en caso de error total
			return "Error al generar reporte PDF".getBytes();
		}
	}
}
package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.Anexo;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandado;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandante;
import pe.gob.pj.rapidemanda.domain.model.servicio.Firma;
import pe.gob.pj.rapidemanda.domain.model.servicio.Fundamentacion;
import pe.gob.pj.rapidemanda.domain.model.servicio.Petitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReportePdfPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDocumentoUseCasePort;

@Slf4j
@Component("gestionReportePdfPersistencePort")
public class GestionReportePdfPersistenceAdapter implements GestionReportePdfPersistencePort {

	@Autowired
	@Qualifier("gestionDemandaPersistencePort")
	private GestionDemandaPersistencePort gestionDemandaPersistencePort;

	@Autowired
	@Qualifier("gestionDocumentoUseCasePort")
	private GestionDocumentoUseCasePort gestionDocumentoUseCasePort;

	// Colores corporativos según diseño oficial
	private static final DeviceRgb COLOR_HEADER = new DeviceRgb(196, 30, 58); // Rojo corporativo oficial
	private static final DeviceRgb COLOR_ACCENT = new DeviceRgb(220, 53, 69); // Rojo claro
	private static final DeviceRgb COLOR_TEXT = new DeviceRgb(33, 37, 41); // Gris oscuro profesional
	private static final DeviceRgb COLOR_WATERMARK = new DeviceRgb(200, 200, 200); // Gris claro para marca de agua

	@Override
	public byte[] generarReporteDemanda(String cuo, Integer idDemanda) throws Exception {
		log.info("{} Iniciando generación de reporte PDF para demanda ID: {}", cuo, idDemanda);

		try {
			// Obtener datos de la demanda
			Map<String, Object> filters = Map.of("id", idDemanda);
			List<Demanda> demandas = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);

			if (demandas.isEmpty()) {
				throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
						String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()));
			}

			Demanda demanda = demandas.get(0);

			// Generar PDF
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdfDoc = new PdfDocument(writer);
			
			// Configurar tamaño de página
			pdfDoc.setDefaultPageSize(PageSize.A4);
			
			// Agregar manejador de eventos para marca de agua y paginación
			pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new WatermarkEventHandler());

			Document document = new Document(pdfDoc);
			document.setMargins(50, 50, 80, 50); // top, right, bottom, left

			// Generar contenido del PDF
			generarEncabezado(document, demanda);
			generarDatosGenerales(document, demanda);
			generarDemandantes(document, demanda);
			generarDemandados(document, demanda);
			generarPetitorios(document, demanda);
			generarRelacionLaboral(document, demanda);
			generarFundamentaciones(document, demanda);
			generarAnexos(document, demanda);
			generarFirmas(document, demanda, cuo);

			document.close();

			log.info("{} Reporte PDF generado exitosamente para demanda ID: {}, tamaño: {} bytes", 
					cuo, idDemanda, baos.size());

			return baos.toByteArray();

		} catch (ErrorException e) {
			log.error("{} Error de negocio al generar reporte PDF para demanda ID: {}", cuo, idDemanda, e);
			throw e;
		} catch (Exception e) {
			log.error("{} Error inesperado al generar reporte PDF para demanda ID: {}", cuo, idDemanda, e);
			throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REPORTE_PDF.getNombre()));
		}
	}

	@Override
	public byte[] obtenerImagenAlfresco(String cuo, String archivoId) throws Exception {
		try {
			return gestionDocumentoUseCasePort.descargarDocumento(cuo, archivoId);
		} catch (Exception e) {
			log.warn("{} No se pudo obtener imagen de Alfresco con ID: {}", cuo, archivoId, e);
			return null;
		}
	}

	/**
	 * Genera el encabezado del documento con logos laterales y título centrado
	 */
	private void generarEncabezado(Document document, Demanda demanda) throws IOException {
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Tabla principal del encabezado con 3 columnas: Logo izq, Título centro, Logo der
		Table encabezadoTable = new Table(UnitValue.createPercentArray(new float[]{15, 70, 15}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(0);

		// Celda del logo izquierdo (Poder Judicial)
		Cell logoIzqCell = new Cell();
		try {
			byte[] logoIzqBytes = getClass().getClassLoader()
					.getResourceAsStream("images/logopj.png").readAllBytes();
			ImageData logoIzqData = ImageDataFactory.create(logoIzqBytes);
			Image logoIzq = new Image(logoIzqData)
					.setWidth(60)
					.setHeight(60);
			logoIzqCell.add(logoIzq);
		} catch (Exception e) {
			log.warn("No se pudo cargar el logo del Poder Judicial: {}", e.getMessage());
			logoIzqCell.add(new Paragraph("PJ").setFont(fontBold).setFontSize(12).setTextAlignment(TextAlignment.CENTER));
		}
		logoIzqCell.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);

		// Celda del título central
		Cell tituloCentralCell = new Cell();
		
		// Título principal centrado
		Paragraph titulo1 = new Paragraph("Corte Superior de Justicia de Junín")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_TEXT)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(2);
		
		Paragraph titulo2 = new Paragraph("Comisión de Gestión de Despacho")
				.setFont(fontBold)
				.setFontSize(12)
				.setFontColor(COLOR_TEXT)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(0);
		
		tituloCentralCell.add(titulo1);
		tituloCentralCell.add(titulo2);
		tituloCentralCell.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);

		// Celda del logo derecho (ETIINLPT)
		Cell logoDeCell = new Cell();
		try {
			byte[] logoDeBytes = getClass().getClassLoader()
					.getResourceAsStream("images/logo-etiinlpt.jpg").readAllBytes();
			ImageData logoDeData = ImageDataFactory.create(logoDeBytes);
			Image logoDer = new Image(logoDeData)
					.setWidth(60)
					.setHeight(60);
			logoDeCell.add(logoDer);
		} catch (Exception e) {
			log.warn("No se pudo cargar el logo ETIINLPT: {}", e.getMessage());
			logoDeCell.add(new Paragraph("ETIINLPT").setFont(fontBold).setFontSize(8).setTextAlignment(TextAlignment.CENTER));
		}
		logoDeCell.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);

		// Agregar celdas a la tabla
		encabezadoTable.addCell(logoIzqCell);
		encabezadoTable.addCell(tituloCentralCell);
		encabezadoTable.addCell(logoDeCell);

		document.add(encabezadoTable);

		// Banda roja con el título de la demanda
		Table bandaRojaTable = new Table(UnitValue.createPercentArray(new float[]{100}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginTop(10)
				.setMarginBottom(15);

		Cell bandaRojaCell = new Cell();
		Paragraph tituloDemanda = new Paragraph("DEMANDA LABORAL ANTE EL JUZGADO DE PAZ LETRADO")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(ColorConstants.WHITE)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(4)
				.setMarginBottom(2);
		Paragraph tituloDemanda2 = new Paragraph("(HASTA 70 URP)")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(ColorConstants.WHITE)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(2)
				.setMarginBottom(4);
		
		bandaRojaCell.add(tituloDemanda);
		bandaRojaCell.add(tituloDemanda2);
		bandaRojaCell.setBackgroundColor(COLOR_HEADER)
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.CENTER);

		bandaRojaTable.addCell(bandaRojaCell);
		document.add(bandaRojaTable);

		// Información de generación
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Paragraph fechaGeneracion = new Paragraph("Generado el: " + sdf.format(new Date()))
				.setFont(fontRegular)
				.setFontSize(10)
				.setTextAlignment(TextAlignment.RIGHT)
				.setMarginBottom(20);
		document.add(fechaGeneracion);
	}

	/**
	 * Genera la sección de datos generales
	 */
	private void generarDatosGenerales(Document document, Demanda demanda) throws IOException {
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("DATOS GENERALES DE LA DEMANDA")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Tabla de datos generales
		Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
				.setWidth(UnitValue.createPercentValue(100));

		agregarFilaTabla(tabla, "Código de Demanda:", String.valueOf(demanda.getId()), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Sumilla:", demanda.getSumilla(), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Estado:", demanda.getEstadoDemanda(), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Tipo Presentación:", demanda.getTipoPresentacion(), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Usuario:", demanda.getUsuarioDemanda(), fontBold, fontRegular);

		document.add(tabla);
		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de demandantes
	 */
	private void generarDemandantes(Document document, Demanda demanda) throws IOException {
		if (demanda.getDemandantes() == null || demanda.getDemandantes().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("DEMANDANTES")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getDemandantes().size(); i++) {
			Demandante demandante = demanda.getDemandantes().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("Demandante " + (i + 1))
					.setFont(fontBold)
					.setFontSize(12)
					.setFontColor(COLOR_ACCENT)
					.setMarginBottom(5);
			document.add(subtitulo);

			// Tabla de datos del demandante
			Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
					.setWidth(UnitValue.createPercentValue(100));

			agregarFilaTabla(tabla, "Tipo Documento:", demandante.getTipoDocumento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Número Documento:", demandante.getNumeroDocumento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Razón Social:", demandante.getRazonSocial(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Género:", demandante.getGenero(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Fecha Nacimiento:", demandante.getFechaNacimiento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Departamento:", demandante.getDepartamento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Provincia:", demandante.getProvincia(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Distrito:", demandante.getDistrito(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Domicilio:", demandante.getTipoDomicilio() + " " + demandante.getDomicilio(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Referencia:", demandante.getReferencia(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Correo:", demandante.getCorreo(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Celular:", demandante.getCelular(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Casilla Electrónica:", demandante.getCasillaElectronica(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Apoderado Común:", "1".equals(demandante.getApoderadoComun()) ? "Sí" : "No", fontBold, fontRegular);

			document.add(tabla);
			document.add(new Paragraph().setMarginBottom(10));
		}
	}

	/**
	 * Genera la sección de demandados
	 */
	private void generarDemandados(Document document, Demanda demanda) throws IOException {
		if (demanda.getDemandados() == null || demanda.getDemandados().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("DEMANDADOS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getDemandados().size(); i++) {
			Demandado demandado = demanda.getDemandados().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("Demandado " + (i + 1))
					.setFont(fontBold)
					.setFontSize(12)
					.setFontColor(COLOR_ACCENT)
					.setMarginBottom(5);
			document.add(subtitulo);

			// Tabla de datos del demandado
			Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
					.setWidth(UnitValue.createPercentValue(100));

			agregarFilaTabla(tabla, "Tipo Documento:", demandado.getTipoDocumento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Número Documento:", demandado.getNumeroDocumento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Razón Social:", demandado.getRazonSocial(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Departamento:", demandado.getDepartamento(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Provincia:", demandado.getProvincia(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Distrito:", demandado.getDistrito(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Domicilio:", demandado.getTipoDomicilio() + " " + demandado.getDomicilio(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Referencia:", demandado.getReferencia(), fontBold, fontRegular);

			document.add(tabla);
			document.add(new Paragraph().setMarginBottom(10));
		}
	}

	/**
	 * Genera la sección de petitorios
	 */
	private void generarPetitorios(Document document, Demanda demanda) throws IOException {
		if (demanda.getPetitorios() == null || demanda.getPetitorios().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("PETITORIOS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getPetitorios().size(); i++) {
			Petitorio petitorio = demanda.getPetitorios().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("Petitorio " + (i + 1))
					.setFont(fontBold)
					.setFontSize(12)
					.setFontColor(COLOR_ACCENT)
					.setMarginBottom(5);
			document.add(subtitulo);

			// Tabla de datos del petitorio
			Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
					.setWidth(UnitValue.createPercentValue(100));

			agregarFilaTabla(tabla, "Tipo:", petitorio.getTipo(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Pretensión Principal:", petitorio.getPretensionPrincipal(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Concepto:", petitorio.getConcepto(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Pretensión Accesoria:", petitorio.getPretensionAccesoria(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Monto:", petitorio.getMonto() != null ? "S/ " + petitorio.getMonto().toString() : "", fontBold, fontRegular);
			agregarFilaTabla(tabla, "Justificación:", petitorio.getJustificacion(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Fecha Inicio:", petitorio.getFechaInicio(), fontBold, fontRegular);
			agregarFilaTabla(tabla, "Fecha Fin:", petitorio.getFechaFin(), fontBold, fontRegular);

			document.add(tabla);
			document.add(new Paragraph().setMarginBottom(10));
		}
	}

	/**
	 * Genera la sección de relación laboral
	 */
	private void generarRelacionLaboral(Document document, Demanda demanda) throws IOException {
		if (demanda.getRelacionLaboral() == null) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("RELACIÓN LABORAL")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		RelacionLaboral relacion = demanda.getRelacionLaboral();

		// Tabla de datos de la relación laboral
		Table tabla = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
				.setWidth(UnitValue.createPercentValue(100));

		agregarFilaTabla(tabla, "Régimen:", relacion.getRegimen(), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Fecha Inicio:", relacion.getFechaInicio(), fontBold, fontRegular);
		agregarFilaTabla(tabla, "Fecha Fin:", relacion.getFechaFin(), fontBold, fontRegular);
		
		String tiempo = "";
		if (relacion.getAnios() != null && relacion.getAnios() > 0) {
			tiempo += relacion.getAnios() + " años ";
		}
		if (relacion.getMeses() != null && relacion.getMeses() > 0) {
			tiempo += relacion.getMeses() + " meses ";
		}
		if (relacion.getDias() != null && relacion.getDias() > 0) {
			tiempo += relacion.getDias() + " días";
		}
		agregarFilaTabla(tabla, "Tiempo de Servicio:", tiempo.trim(), fontBold, fontRegular);
		
		agregarFilaTabla(tabla, "Remuneración:", relacion.getRemuneracion() != null ? "S/ " + relacion.getRemuneracion().toString() : "", fontBold, fontRegular);

		document.add(tabla);
		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de fundamentaciones
	 */
	private void generarFundamentaciones(Document document, Demanda demanda) throws IOException {
		if (demanda.getFundamentaciones() == null || demanda.getFundamentaciones().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("FUNDAMENTACIONES")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getFundamentaciones().size(); i++) {
			Fundamentacion fundamentacion = demanda.getFundamentaciones().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("Fundamentación " + (i + 1))
					.setFont(fontBold)
					.setFontSize(12)
					.setFontColor(COLOR_ACCENT)
					.setMarginBottom(5);
			document.add(subtitulo);

			// Contenido de la fundamentación
			Paragraph contenido = new Paragraph(fundamentacion.getContenido())
					.setFont(fontRegular)
					.setFontSize(11)
					.setTextAlignment(TextAlignment.JUSTIFIED)
					.setMarginBottom(10);
			document.add(contenido);
		}

		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de anexos
	 */
	private void generarAnexos(Document document, Demanda demanda) throws IOException {
		if (demanda.getAnexos() == null || demanda.getAnexos().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("ANEXOS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Tabla de anexos
		Table tabla = new Table(UnitValue.createPercentArray(new float[]{20, 60, 20}))
				.setWidth(UnitValue.createPercentValue(100));

		// Encabezados
		tabla.addHeaderCell(new Cell().add(new Paragraph("Tipo").setFont(fontBold).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER).setTextAlignment(TextAlignment.CENTER));
		tabla.addHeaderCell(new Cell().add(new Paragraph("Descripción").setFont(fontBold).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER).setTextAlignment(TextAlignment.CENTER));
		tabla.addHeaderCell(new Cell().add(new Paragraph("Incluido").setFont(fontBold).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER).setTextAlignment(TextAlignment.CENTER));

		for (Anexo anexo : demanda.getAnexos()) {
			tabla.addCell(new Cell().add(new Paragraph(anexo.getTipo()).setFont(fontRegular)));
			tabla.addCell(new Cell().add(new Paragraph("Anexo de la demanda").setFont(fontRegular)));
			tabla.addCell(new Cell().add(new Paragraph("1".equals(anexo.getIncluido()) ? "Sí" : "No").setFont(fontRegular))
					.setTextAlignment(TextAlignment.CENTER));
		}

		document.add(tabla);
		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de firmas con imágenes de Alfresco
	 */
	private void generarFirmas(Document document, Demanda demanda, String cuo) throws IOException {
		if (demanda.getFirmas() == null || demanda.getFirmas().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("FIRMAS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getFirmas().size(); i++) {
			Firma firma = demanda.getFirmas().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("Firma " + (i + 1) + " - " + firma.getTipo())
					.setFont(fontBold)
					.setFontSize(12)
					.setFontColor(COLOR_ACCENT)
					.setMarginBottom(5);
			document.add(subtitulo);

			// Intentar obtener y mostrar la imagen de la firma
			try {
				byte[] imagenBytes = obtenerImagenAlfresco(cuo, firma.getArchivoUrl());
				if (imagenBytes != null && imagenBytes.length > 0) {
					ImageData imageData = ImageDataFactory.create(imagenBytes);
					Image imagen = new Image(imageData);
					
					// Redimensionar imagen si es necesario
					imagen.setMaxWidth(200);
					imagen.setMaxHeight(100);
					imagen.setMarginBottom(10);
					
					document.add(imagen);
				} else {
					// Si no se puede obtener la imagen, mostrar texto alternativo
					Paragraph textoAlternativo = new Paragraph("Firma digital (ID: " + firma.getArchivoUrl() + ")")
							.setFont(fontRegular)
							.setFontSize(10)
							.setFontColor(ColorConstants.GRAY)
							.setMarginBottom(10);
					document.add(textoAlternativo);
				}
			} catch (Exception e) {
				log.warn("{} Error al obtener imagen de firma con ID: {}", cuo, firma.getArchivoUrl(), e);
				// Mostrar texto alternativo en caso de error
				Paragraph textoAlternativo = new Paragraph("Firma digital (ID: " + firma.getArchivoUrl() + ")")
						.setFont(fontRegular)
						.setFontSize(10)
						.setFontColor(ColorConstants.GRAY)
						.setMarginBottom(10);
				document.add(textoAlternativo);
			}
		}

		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Método auxiliar para agregar filas a las tablas
	 */
	private void agregarFilaTabla(Table tabla, String etiqueta, String valor, PdfFont fontBold, PdfFont fontRegular) {
		tabla.addCell(new Cell().add(new Paragraph(etiqueta).setFont(fontBold).setFontSize(10))
				.setBorder(Border.NO_BORDER).setPaddingBottom(5));
		tabla.addCell(new Cell().add(new Paragraph(valor != null ? valor : "").setFont(fontRegular).setFontSize(10))
				.setBorder(Border.NO_BORDER).setPaddingBottom(5));
	}

	/**
	 * Manejador de eventos para marca de agua y paginación
	 */
	private static class WatermarkEventHandler implements IEventHandler {
		@Override
		public void handleEvent(Event event) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
			PdfDocument pdfDoc = docEvent.getDocument();
			PdfPage page = docEvent.getPage();
			PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

			try {
				// Configurar transparencia para marca de agua
				PdfExtGState gState = new PdfExtGState();
				gState.setFillOpacity(0.1f);
				canvas.setExtGState(gState);

				// Agregar marca de agua en dos líneas
				PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
				
				// Textos de la marca de agua en dos líneas
				String linea1 = "JUZGADO DE PAZ LETRADO";
				String linea2 = "LABORAL DE HUANCAYO";
				float fontSize = 40; // Tamaño reducido para que quepa mejor
				
				// Calcular posición central y aplicar rotación
				float centerX = page.getPageSize().getWidth() / 2;
				float centerY = page.getPageSize().getHeight() / 2;
				
				canvas.saveState();
				canvas.concatMatrix(
					(float) Math.cos(Math.toRadians(45)), (float) Math.sin(Math.toRadians(45)),
					(float) -Math.sin(Math.toRadians(45)), (float) Math.cos(Math.toRadians(45)),
					centerX, centerY
				);
				
				// Calcular posiciones para centrar ambas líneas
				float linea1Width = font.getWidth(linea1, fontSize);
				float linea2Width = font.getWidth(linea2, fontSize);
				float lineHeight = font.getAscent(linea1, fontSize) - font.getDescent(linea1, fontSize);
				
				// Primera línea (arriba)
				canvas.beginText()
						.setFontAndSize(font, fontSize)
						.setColor(COLOR_WATERMARK, true)
						.moveText(-linea1Width / 2, lineHeight / 2)
						.showText(linea1)
						.endText();
				
				// Segunda línea (abajo)
				canvas.beginText()
						.setFontAndSize(font, fontSize)
						.setColor(COLOR_WATERMARK, true)
						.moveText(-linea2Width / 2, -lineHeight / 2)
						.showText(linea2)
						.endText();
				
				canvas.restoreState();

				// Restaurar opacidad normal
				gState.setFillOpacity(1.0f);
				canvas.setExtGState(gState);

				// Agregar número de página
				Rectangle pageSize = page.getPageSize();
				String pageText = "Página " + pdfDoc.getPageNumber(page);
				float pageTextWidth = font.getWidth(pageText, 10);
				
				canvas.beginText()
						.setFontAndSize(font, 10)
						.setColor(COLOR_TEXT, true)
						.moveText(pageSize.getWidth() - 50 - pageTextWidth, 30)
						.showText(pageText)
						.endText();

			} catch (IOException e) {
				// Log error but don't throw to avoid breaking PDF generation
				log.error("Error al agregar marca de agua: {}", e.getMessage());
			}

			canvas.release();
		}
	}
}
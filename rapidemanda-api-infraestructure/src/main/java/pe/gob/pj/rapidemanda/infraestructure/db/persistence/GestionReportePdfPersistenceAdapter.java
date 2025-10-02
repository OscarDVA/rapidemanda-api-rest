package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.itextpdf.layout.properties.VerticalAlignment;

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
			document.setMargins(30, 30, 60, 30); // top, right, bottom, left

			// Generar contenido del PDF
			generarEncabezado(document, demanda);
			generarDatosGenerales(document, demanda);
			generarDemandantes(document, demanda);
			generarDemandados(document, demanda);
			generarPetitorios(document, demanda);
			generarRelacionLaboral(document, demanda);
			generarJustificacionPetitorios(document, demanda);
			generarFundamentaciones(document, demanda);
			generarViaProcedimental(document, demanda);
			generarMediosProbatorios(document, demanda);
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
	 * Genera el encabezado del reporte con logos y títulos institucionales
	 */
	private void generarEncabezado(Document document, Demanda demanda) throws IOException {
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Tabla principal del encabezado con 3 columnas: logo izq, título central, logo der
		Table encabezadoTable = new Table(UnitValue.createPercentArray(new float[]{20, 60, 20}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		// Logo izquierdo (Poder Judicial)
		try {
			byte[] logoIzqBytes = getClass().getResourceAsStream("/images/logopj.png").readAllBytes();
			ImageData logoIzqData = ImageDataFactory.create(logoIzqBytes);
			Image logoIzq = new Image(logoIzqData).setWidth(80).setHeight(80);
			Cell logoIzqCell = new Cell().add(logoIzq).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
			encabezadoTable.addCell(logoIzqCell);
		} catch (Exception e) {
			log.warn("No se pudo cargar el logo izquierdo: {}", e.getMessage());
			Cell logoIzqCell = new Cell().add(new Paragraph("LOGO PJ")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
			encabezadoTable.addCell(logoIzqCell);
		}

		// Título central
		Paragraph titulo1 = new Paragraph("Corte Superior de Justicia de Junín")
				.setFont(fontBold)
				.setFontSize(14)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(2);

		Paragraph titulo2 = new Paragraph("Comisión de Gestión de Despacho")
				.setFont(fontBold)
				.setFontSize(12)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(0);

		Cell tituloCell = new Cell()
				.add(titulo1)
				.add(titulo2)
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
		encabezadoTable.addCell(tituloCell);

		// Logo derecho (ETIINLPT)
		try {
			byte[] logoderBytes = getClass().getResourceAsStream("/images/logo-etiinlpt.jpg").readAllBytes();
			ImageData logoderData = ImageDataFactory.create(logoderBytes);
			Image logoDer = new Image(logoderData).setWidth(80).setHeight(80);
			Cell logoderCell = new Cell().add(logoDer).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
			encabezadoTable.addCell(logoderCell);
		} catch (Exception e) {
			log.warn("No se pudo cargar el logo derecho: {}", e.getMessage());
			Cell logoderCell = new Cell().add(new Paragraph("ETIINLPT")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
			encabezadoTable.addCell(logoderCell);
		}

		document.add(encabezadoTable);

		// Banda roja con título de la demanda
		Table bandaRojaTable = new Table(UnitValue.createPercentArray(new float[]{100}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(15);

		Paragraph tituloDemanda = new Paragraph("DEMANDA LABORAL ANTE EL JUZGADO DE PAZ LETRADO")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(ColorConstants.WHITE)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(4)
				.setMarginBottom(2);

		Paragraph tituloDemanda2 = new Paragraph("(HASTA 70 URP)")
				.setFont(fontRegular)
				.setFontSize(14)
				.setFontColor(ColorConstants.WHITE)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginTop(2)
				.setMarginBottom(4);
		
		Cell bandaRojaCell = new Cell();
		bandaRojaCell.add(tituloDemanda);
		bandaRojaCell.add(tituloDemanda2);
		bandaRojaCell.setBackgroundColor(COLOR_HEADER)
				.setBorder(Border.NO_BORDER)
				.setTextAlignment(TextAlignment.CENTER);

		bandaRojaTable.addCell(bandaRojaCell);
		document.add(bandaRojaTable);

		// CÓDIGO ÚNICO DESTACADO (como en el modelo)
		Table codigoTable = new Table(UnitValue.createPercentArray(new float[]{25, 50, 25}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(15);

		// Celda izquierda vacía
		Cell celdaIzq = new Cell().setBorder(Border.NO_BORDER);
		codigoTable.addCell(celdaIzq);

		// Celda central con código único
		Paragraph codigoLabel = new Paragraph("CÓDIGO ÚNICO")
				.setFont(fontBold)
				.setFontSize(10)
				.setTextAlignment(TextAlignment.CENTER)
				.setMarginBottom(2);

		Paragraph codigoValor = new Paragraph(String.valueOf(demanda.getId()))
				.setFont(fontBold)
				.setFontSize(24)
				.setTextAlignment(TextAlignment.CENTER)
				.setFontColor(COLOR_HEADER)
				.setMarginTop(0)
				.setMarginBottom(2);

		Cell codigoCentral = new Cell()
				.add(codigoLabel)
				.add(codigoValor)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(COLOR_HEADER, 2))
				.setTextAlignment(TextAlignment.CENTER)
				.setPadding(8);
		codigoTable.addCell(codigoCentral);

		// Celda derecha vacía
		Cell celdaDer = new Cell().setBorder(Border.NO_BORDER);
		codigoTable.addCell(celdaDer);

		document.add(codigoTable);

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

		// Título de sección con numeración
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
	 * Genera la sección de demandantes con layout compacto
	 */
	private void generarDemandantes(Document document, Demanda demanda) throws IOException {
		if (demanda.getDemandantes() == null || demanda.getDemandantes().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección con numeración
		Paragraph tituloSeccion = new Paragraph("1. DATOS DEL DEMANDANTE")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getDemandantes().size(); i++) {
			Demandante demandante = demanda.getDemandantes().get(i);

			// Subtítulo solo si hay múltiples demandantes
			if (demanda.getDemandantes().size() > 1) {
				Paragraph subtitulo = new Paragraph("1." + (i + 1) + " Demandante " + (i + 1))
						.setFont(fontBold)
						.setFontSize(12)
						.setFontColor(COLOR_ACCENT)
						.setMarginBottom(5);
				document.add(subtitulo);
			}

			// Layout compacto con múltiples columnas
			generarLayoutCompactoDemandante(document, demandante, fontBold, fontRegular);
			document.add(new Paragraph().setMarginBottom(10));
		}
	}

	/**
	 * Genera un layout compacto para los datos del demandante
	 */
	private void generarLayoutCompactoDemandante(Document document, Demandante demandante, PdfFont fontBold, PdfFont fontRegular) {
		// Fila 1: Datos de identificación (5 columnas)
		Table fila1 = new Table(UnitValue.createPercentArray(new float[]{18, 18, 18, 18, 28}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		agregarCeldaFormulario(fila1, "TIPO DE DOCUMENTO", demandante.getTipoDocumento(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "NÚMERO DE DOCUMENTO", demandante.getNumeroDocumento(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "GÉNERO", demandante.getGenero(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "FECHA DE NACIMIENTO", demandante.getFechaNacimiento(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "NÚMERO DE CELULAR", demandante.getCelular(), fontBold, fontRegular);

		document.add(fila1);

		// Fila 2: Nombres y correo (2 columnas)
		Table fila2 = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		agregarCeldaFormulario(fila2, "NOMBRES Y APELLIDOS", demandante.getRazonSocial(), fontBold, fontRegular);
		agregarCeldaFormulario(fila2, "CORREO ELECTRÓNICO", demandante.getCorreo(), fontBold, fontRegular);

		document.add(fila2);

		// Fila 3: Ubicación geográfica (3 columnas)
		Table fila3 = new Table(UnitValue.createPercentArray(new float[]{33, 33, 34}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		agregarCeldaFormulario(fila3, "DEPARTAMENTO", demandante.getDepartamento(), fontBold, fontRegular);
		agregarCeldaFormulario(fila3, "PROVINCIA", demandante.getProvincia(), fontBold, fontRegular);
		agregarCeldaFormulario(fila3, "DISTRITO", demandante.getDistrito(), fontBold, fontRegular);

		document.add(fila3);

		// Fila 4: Domicilio y referencia (2 columnas)
		Table fila4 = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		String domicilioCompleto = (demandante.getTipoDomicilio() != null ? demandante.getTipoDomicilio() + " " : "") + 
								  (demandante.getDomicilio() != null ? demandante.getDomicilio() : "");
		agregarCeldaFormulario(fila4, "DOMICILIO", domicilioCompleto.trim(), fontBold, fontRegular);
		agregarCeldaFormulario(fila4, "REFERENCIA", demandante.getReferencia(), fontBold, fontRegular);

		document.add(fila4);

		// Fila 5: Casilla electrónica (1 columna)
		Table fila5 = new Table(UnitValue.createPercentArray(new float[]{100}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(5);

		agregarCeldaFormulario(fila5, "CASILLA ELECTRÓNICA", demandante.getCasillaElectronica(), fontBold, fontRegular);

		document.add(fila5);
	}

	/**
	 * Agrega una celda con formato de formulario (etiqueta arriba, valor abajo)
	 */
	private void agregarCeldaFormulario(Table tabla, String etiqueta, String valor, PdfFont fontBold, PdfFont fontRegular) {
		// Crear celda contenedora
		Cell celda = new Cell();
		celda.setBorder(Border.NO_BORDER);
		celda.setPadding(3);

		// Etiqueta en la parte superior
		Paragraph labelParagraph = new Paragraph(etiqueta != null ? etiqueta : "")
				.setFont(fontBold)
				.setFontSize(8)
				.setFontColor(COLOR_TEXT)
				.setMarginBottom(1);

		// Valor en la parte inferior con borde
		Paragraph valorParagraph = new Paragraph(valor != null ? valor : "")
				.setFont(fontRegular)
				.setFontSize(9)
				.setFontColor(COLOR_TEXT)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 0.5f))
				.setPadding(3)
				.setMinHeight(15);

		celda.add(labelParagraph);
		celda.add(valorParagraph);

		tabla.addCell(celda);
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
		Paragraph tituloSeccion = new Paragraph("2. DATOS DE LA PARTE DEMANDADA")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getDemandados().size(); i++) {
			Demandado demandado = demanda.getDemandados().get(i);

			// Subtítulo solo si hay múltiples demandados
			if (demanda.getDemandados().size() > 1) {
				Paragraph subtitulo = new Paragraph("2." + (i + 1) + " Demandado " + (i + 1))
						.setFont(fontBold)
						.setFontSize(12)
						.setFontColor(COLOR_ACCENT)
						.setMarginBottom(5);
				document.add(subtitulo);
			}

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
	 * Genera la sección de petitorios con layout compacto tipo tabla
	 */
	private void generarPetitorios(Document document, Demanda demanda) throws IOException {
		if (demanda.getPetitorios() == null || demanda.getPetitorios().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección con numeración
		Paragraph tituloSeccion = new Paragraph("3. PETITORIOS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Tabla compacta de petitorios con columnas: Tipo, Pretensión Principal, Conceptos, Monto
		Table tabla = new Table(UnitValue.createPercentArray(new float[]{15, 35, 30, 20}))
				.setWidth(UnitValue.createPercentValue(100))
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.BLACK, 1));

		// Encabezados de la tabla
		Cell headerTipo = new Cell()
				.add(new Paragraph("TIPO").setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER)
				.setTextAlignment(TextAlignment.CENTER)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.WHITE, 1));
		tabla.addHeaderCell(headerTipo);

		Cell headerPretension = new Cell()
				.add(new Paragraph("PRETENSIÓN PRINCIPAL").setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER)
				.setTextAlignment(TextAlignment.CENTER)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.WHITE, 1));
		tabla.addHeaderCell(headerPretension);

		Cell headerConceptos = new Cell()
				.add(new Paragraph("CONCEPTOS").setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER)
				.setTextAlignment(TextAlignment.CENTER)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.WHITE, 1));
		tabla.addHeaderCell(headerConceptos);

		Cell headerMonto = new Cell()
				.add(new Paragraph("MONTO").setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
				.setBackgroundColor(COLOR_HEADER)
				.setTextAlignment(TextAlignment.CENTER)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.WHITE, 1));
		tabla.addHeaderCell(headerMonto);

		// Variable para calcular el total
		double totalBeneficios = 0.0;

		// Filas de datos
		for (Petitorio petitorio : demanda.getPetitorios()) {
			// Celda Tipo
			Cell celdaTipo = new Cell()
					.add(new Paragraph(petitorio.getTipo() != null ? petitorio.getTipo() : "").setFont(fontRegular).setFontSize(8))
					.setTextAlignment(TextAlignment.CENTER)
					.setPadding(4)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 0.5f));
			tabla.addCell(celdaTipo);

			// Celda Pretensión Principal
			String pretension = "";
			if (petitorio.getPretensionPrincipal() != null) {
				pretension = petitorio.getPretensionPrincipal();
				// Agregar pretensión accesoria si existe
				if (petitorio.getPretensionAccesoria() != null && !petitorio.getPretensionAccesoria().trim().isEmpty()) {
					pretension += "\n" + petitorio.getPretensionAccesoria();
				}
			}
			Cell celdaPretension = new Cell()
					.add(new Paragraph(pretension).setFont(fontRegular).setFontSize(8))
					.setTextAlignment(TextAlignment.LEFT)
					.setPadding(4)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 0.5f));
			tabla.addCell(celdaPretension);

			// Celda Conceptos
			String conceptos = "";
			if (petitorio.getConcepto() != null) {
				conceptos = petitorio.getConcepto();
			}
			// Agregar fechas si existen
			if (petitorio.getFechaInicio() != null || petitorio.getFechaFin() != null) {
				String fechas = "";
				if (petitorio.getFechaInicio() != null) {
					fechas += "Desde: " + petitorio.getFechaInicio();
				}
				if (petitorio.getFechaFin() != null) {
					if (!fechas.isEmpty()) fechas += "\n";
					fechas += "Hasta: " + petitorio.getFechaFin();
				}
				if (!conceptos.isEmpty() && !fechas.isEmpty()) {
					conceptos += "\n" + fechas;
				} else if (!fechas.isEmpty()) {
					conceptos = fechas;
				}
			}
			Cell celdaConceptos = new Cell()
					.add(new Paragraph(conceptos).setFont(fontRegular).setFontSize(8))
					.setTextAlignment(TextAlignment.LEFT)
					.setPadding(4)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 0.5f));
			tabla.addCell(celdaConceptos);

			// Celda Monto
			BigDecimal totalBeneficios1 = BigDecimal.ZERO;
			String montoTexto = "";
			if (petitorio.getMonto() != null) {
				montoTexto = "S/ " + String.format("%.2f", petitorio.getMonto());
				totalBeneficios1 = totalBeneficios1.add(petitorio.getMonto());

			}
			Cell celdaMonto = new Cell()
					.add(new Paragraph(montoTexto).setFont(fontRegular).setFontSize(8))
					.setTextAlignment(TextAlignment.RIGHT)
					.setPadding(4)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.GRAY, 0.5f));
			tabla.addCell(celdaMonto);
		}

		// Fila del total
		Cell totalLabel = new Cell(1, 3)
				.add(new Paragraph("TOTAL DE BENEFICIOS RECLAMADOS").setFont(fontBold).setFontSize(9))
				.setBackgroundColor(new DeviceRgb(240, 240, 240))
				.setTextAlignment(TextAlignment.RIGHT)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.BLACK, 1));
		tabla.addCell(totalLabel);

		Cell totalMonto = new Cell()
				.add(new Paragraph("S/ " + String.format("%.2f", totalBeneficios)).setFont(fontBold).setFontSize(10).setFontColor(COLOR_HEADER))
				.setBackgroundColor(new DeviceRgb(240, 240, 240))
				.setTextAlignment(TextAlignment.RIGHT)
				.setPadding(5)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(ColorConstants.BLACK, 1));
		tabla.addCell(totalMonto);

		document.add(tabla);
		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de relación laboral con formato compacto
	 */
	private void generarRelacionLaboral(Document document, Demanda demanda) throws IOException {
		if (demanda.getRelacionLaboral() == null) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("4. DATOS DE LA RELACIÓN LABORAL")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		RelacionLaboral relacion = demanda.getRelacionLaboral();

		// Layout compacto para relación laboral
		generarLayoutCompactoRelacionLaboral(document, relacion, fontBold, fontRegular);
		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera un layout compacto para los datos de relación laboral
	 */
	private void generarLayoutCompactoRelacionLaboral(Document document, RelacionLaboral relacion, PdfFont fontBold, PdfFont fontRegular) {
		// Fila 1: Régimen laboral, fechas de inicio y fin (3 columnas)
		Table fila1 = new Table(UnitValue.createPercentArray(new float[]{33, 33, 34}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(2);

		agregarCeldaFormulario(fila1, "RÉGIMEN LABORAL", relacion.getRegimen(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "FECHA DE INICIO", relacion.getFechaInicio(), fontBold, fontRegular);
		agregarCeldaFormulario(fila1, "FECHA FIN", relacion.getFechaFin(), fontBold, fontRegular);

		document.add(fila1);

		// Fila 2: Tiempo de servicio desglosado y monto de remuneración (4 columnas)
		Table fila2 = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 40}))
				.setWidth(UnitValue.createPercentValue(100))
				.setMarginBottom(5);

		String anios = relacion.getAnios() != null ? relacion.getAnios().toString() : "0";
		String meses = relacion.getMeses() != null ? relacion.getMeses().toString() : "0";
		String dias = relacion.getDias() != null ? relacion.getDias().toString() : "0";
		String remuneracion = relacion.getRemuneracion() != null ? "S/. " + String.format("%.2f", relacion.getRemuneracion()) : "";

		agregarCeldaFormulario(fila2, "AÑOS", anios, fontBold, fontRegular);
		agregarCeldaFormulario(fila2, "MESES", meses, fontBold, fontRegular);
		agregarCeldaFormulario(fila2, "DÍAS", dias, fontBold, fontRegular);
		agregarCeldaFormulario(fila2, "MONTO - ÚLTIMA REMUNERACIÓN", remuneracion, fontBold, fontRegular);

		document.add(fila2);
	}

	/**
	 * Genera la sección de justificación de petitorios ordenada
	 */
	private void generarJustificacionPetitorios(Document document, Demanda demanda) throws IOException {
		if (demanda.getPetitorios() == null || demanda.getPetitorios().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("5. JUSTIFICACIÓN DE PETITORIOS (HECHOS QUE FUNDAMENTAN EL PETITORIO)")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Generar justificaciones ordenadas por petitorio
		char letra = 'A';
		for (Petitorio petitorio : demanda.getPetitorios()) {
			if (petitorio.getJustificacion() != null && !petitorio.getJustificacion().trim().isEmpty()) {
				// Crear párrafo con letra identificadora
				Paragraph justificacion = new Paragraph()
						.setFont(fontRegular)
						.setFontSize(11)
						.setTextAlignment(TextAlignment.JUSTIFIED)
						.setMarginBottom(8);

				// Agregar letra en negrita
				justificacion.add(new com.itextpdf.layout.element.Text(String.valueOf(letra) + ". ")
						.setFont(fontBold)
						.setFontSize(11));

				// Agregar texto de justificación
				justificacion.add(new com.itextpdf.layout.element.Text(petitorio.getJustificacion())
						.setFont(fontRegular)
						.setFontSize(11));

				document.add(justificacion);
				letra++;
			}
		}

		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de fundamentaciones con formato numerado
	 */
	private void generarFundamentaciones(Document document, Demanda demanda) throws IOException {
		if (demanda.getFundamentaciones() == null || demanda.getFundamentaciones().isEmpty()) {
			return;
		}

		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("6. FUNDAMENTACIONES")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Generar fundamentaciones con numeración
		for (int i = 0; i < demanda.getFundamentaciones().size(); i++) {
			Fundamentacion fundamentacion = demanda.getFundamentaciones().get(i);

			// Crear párrafo con numeración
			Paragraph contenido = new Paragraph()
					.setFont(fontRegular)
					.setFontSize(11)
					.setTextAlignment(TextAlignment.JUSTIFIED)
					.setMarginBottom(8);

			// Agregar número en negrita
			contenido.add(new com.itextpdf.layout.element.Text((i + 1) + ". ")
					.setFont(fontBold)
					.setFontSize(11));

			// Agregar contenido de la fundamentación
			contenido.add(new com.itextpdf.layout.element.Text(fundamentacion.getContenido())
					.setFont(fontRegular)
					.setFontSize(11));

			document.add(contenido);
		}

		document.add(new Paragraph().setMarginBottom(15));
	}

	/**
	 * Genera la sección de vía procedimental como campo estático
	 */
	private void generarViaProcedimental(Document document, Demanda demanda) throws IOException {
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("7. VÍA PROCEDIMENTAL (CAMPO ESTÁTICO)")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Contenido estático
		Paragraph contenido = new Paragraph("ABREVIADO LABORAL")
				.setFont(fontRegular)
				.setFontSize(11)
				.setTextAlignment(TextAlignment.LEFT)
				.setMarginBottom(15);
		document.add(contenido);
	}

	/**
	 * Genera la sección de medios probatorios concatenando justificaciones de petitorios
	 */
	private void generarMediosProbatorios(Document document, Demanda demanda) throws IOException {
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Título de sección
		Paragraph tituloSeccion = new Paragraph("8. MEDIOS PROBATORIOS (ART. 21° DE LA LEY 29497)")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		// Medios probatorios estáticos
		String[] mediosProbatorios = {
			"1. DOCUMENTOS QUE SUSTENTAN LA PRETENSIÓN DE BENEFICIOS SOCIALES POR NORMA CTS TRUNCO",
			"2. CUMPLIMIENTO DE CONVENIO COLECTIVO POR IDENTIFICACIÓN INSTITUCIONAL",
			"3. DERECHOS LABORALES POR INDEMNIZACIÓN"
		};

		for (String medio : mediosProbatorios) {
			Paragraph medioParrafo = new Paragraph(medio)
					.setFont(fontRegular)
					.setFontSize(11)
					.setTextAlignment(TextAlignment.LEFT)
					.setMarginBottom(5);
			document.add(medioParrafo);
		}

		// Agregar justificaciones de petitorios como medios probatorios adicionales
		if (demanda.getPetitorios() != null && !demanda.getPetitorios().isEmpty()) {
			// Concatenar todas las justificaciones
			StringBuilder justificacionesConcatenadas = new StringBuilder();
			for (Petitorio petitorio : demanda.getPetitorios()) {
				if (petitorio.getJustificacion() != null && !petitorio.getJustificacion().trim().isEmpty()) {
					if (justificacionesConcatenadas.length() > 0) {
						justificacionesConcatenadas.append(" ");
					}
					justificacionesConcatenadas.append(petitorio.getJustificacion());
				}
			}

			// Agregar como medio probatorio adicional si hay justificaciones
			if (justificacionesConcatenadas.length() > 0) {
				Paragraph medioAdicional = new Paragraph("TIPO PETITORIO + JUSTIFICACIÓN: " + justificacionesConcatenadas.toString())
						.setFont(fontRegular)
						.setFontSize(11)
						.setTextAlignment(TextAlignment.JUSTIFIED)
						.setMarginBottom(5);
				document.add(medioAdicional);
			}
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
		Paragraph tituloSeccion = new Paragraph("9. ANEXOS")
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
		Paragraph tituloSeccion = new Paragraph("7. FIRMAS")
				.setFont(fontBold)
				.setFontSize(14)
				.setFontColor(COLOR_HEADER)
				.setMarginBottom(10);
		document.add(tituloSeccion);

		for (int i = 0; i < demanda.getFirmas().size(); i++) {
			Firma firma = demanda.getFirmas().get(i);

			// Subtítulo
			Paragraph subtitulo = new Paragraph("7." + (i + 1) + " Firma " + (i + 1) + " - " + firma.getTipo())
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
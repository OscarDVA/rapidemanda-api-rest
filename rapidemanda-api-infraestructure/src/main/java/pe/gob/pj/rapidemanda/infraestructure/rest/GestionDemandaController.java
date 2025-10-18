package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.port.usecase.AuditoriaGeneralUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDemandaUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.mapper.AuditoriaGeneralMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandaMapper;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRecepcionRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GestionDemandaController implements GestionDemanda, Serializable {
	private static final long serialVersionUID = 1L;

	@Qualifier("gestionDemandaUseCasePort")
	final GestionDemandaUseCasePort gestionDemandaUseCasePort;
	final AuditoriaGeneralUseCasePort auditoriaGeneralUseCasePort;
	final DemandaMapper demandaMapper;
	final AuditoriaGeneralMapper auditoriaGeneralMapper;

	@Override
	public ResponseEntity<GlobalResponse> consultarDemandas(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, String formatoRespuesta, Integer id, String bEstadoId,
			Integer idUsuario, String idTipoPresentacion, String tipoRecepcion, Integer idUsuarioRecepcion,
			String fechaCompletadoInicio, String fechaCompletadoFin) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);

		try {

			Map<String, Object> filters = new HashMap<String, Object>();

			if (id != null) {
				filters.put(Demanda.P_ID, id);
			}
            // Parseo y manejo de estados (acepta múltiples: "C,P")
            List<String> estadosFiltro = null;
            if (bEstadoId != null && !bEstadoId.trim().isEmpty()) {
                estadosFiltro = Arrays.stream(bEstadoId.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();
            }
			if (idUsuario != null) {
				filters.put(Demanda.P_USUARIO, idUsuario);
			}

			if (idTipoPresentacion != null && !idTipoPresentacion.trim().isEmpty()) {
				filters.put(Demanda.P_TIPO_PRESENTACION, idTipoPresentacion);
			}
			if (tipoRecepcion != null && !tipoRecepcion.trim().isEmpty()) {
				filters.put(Demanda.P_TIPO_RECEPCION, tipoRecepcion);
			}
			if (idUsuarioRecepcion != null) {
				filters.put(Demanda.P_USUARIO_RECEPCION, idUsuarioRecepcion);
			}

            // Filtros por rango de fechaCompletado (dd/MM/yyyy)
            try {
                if (fechaCompletadoInicio != null && ProjectUtils.esFechaValida(fechaCompletadoInicio,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date inicio = ProjectUtils.parseStringToDate(fechaCompletadoInicio,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    filters.put(Demanda.P_FECHA_COMPLETADO_INICIO, inicio);
                }
                if (fechaCompletadoFin != null && ProjectUtils.esFechaValida(fechaCompletadoFin,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date fin = ProjectUtils.parseStringToDate(fechaCompletadoFin,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    // Incluir todo el día (23:59:59)
                    fin = ProjectUtils.sumarRestarSegundos(fin, 86399);
                    filters.put(Demanda.P_FECHA_COMPLETADO_FIN, fin);
                }
            } catch (Exception ex) {
                // Ignorar errores de parseo y no aplicar filtros de fecha
            }

            // Considerar estados C y P para filtro por fechas; excluir B
            boolean tieneRangoFechas = filters.containsKey(Demanda.P_FECHA_COMPLETADO_INICIO)
                    && filters.containsKey(Demanda.P_FECHA_COMPLETADO_FIN);

            if (tieneRangoFechas) {
                if (estadosFiltro == null || estadosFiltro.isEmpty()) {
                    // Si no se envían estados, por defecto aplicar C y P
                    filters.put(Demanda.P_ESTADO_IDS, List.of("C", "P"));
                } else {
                    // Excluir B si viene mezclado con otros estados
                    List<String> estadosSinB = estadosFiltro.stream()
                            .filter(e -> !"B".equalsIgnoreCase(e))
                            .toList();
                    if (estadosSinB.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosSinB);
                    } else if (estadosSinB.size() == 1) {
                        filters.put(Demanda.P_ESTADO_ID, estadosSinB.get(0));
                    }
                    // Si todo eran B, no colocar filtro de estado; el rango por fecha excluye B naturalmente
                }
            } else {
                // Sin rango de fechas, mantener comportamiento original
                if (estadosFiltro != null && !estadosFiltro.isEmpty()) {
                    if (estadosFiltro.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosFiltro);
                    } else {
                        filters.put(Demanda.P_ESTADO_ID, estadosFiltro.get(0));
                    }
                }
            }

			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			res.setData(gestionDemandaUseCasePort.buscarDemandas(cuo, filters));
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_CONSULTAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
    return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    // Eliminado: exportarDemandasExcel (hoja única); se mantiene solo multi-hoja

    private String safeStr(String s) { return s != null ? s : ""; }
    // Eliminado: método auxiliar seguro JSON (no usado por exportación multi-hoja)

    @Override
    public ResponseEntity<byte[]> exportarDemandasExcelMultiHoja(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, Integer id, String bEstadoId, Integer idUsuario,
            String idTipoPresentacion, String tipoRecepcion, Integer idUsuarioRecepcion,
            String fechaCompletadoInicio, String fechaCompletadoFin) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (id != null) { filters.put(Demanda.P_ID, id); }
            List<String> estadosFiltro = null;
            if (bEstadoId != null && !bEstadoId.trim().isEmpty()) {
                estadosFiltro = Arrays.stream(bEstadoId.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
            }
            if (idUsuario != null) { filters.put(Demanda.P_USUARIO, idUsuario); }
            if (idTipoPresentacion != null && !idTipoPresentacion.trim().isEmpty()) {
                filters.put(Demanda.P_TIPO_PRESENTACION, idTipoPresentacion.trim());
            }
            if (tipoRecepcion != null && !tipoRecepcion.trim().isEmpty()) {
                filters.put(Demanda.P_TIPO_RECEPCION, tipoRecepcion.trim());
            }
            if (idUsuarioRecepcion != null) { filters.put(Demanda.P_USUARIO_RECEPCION, idUsuarioRecepcion); }

            try {
                if (fechaCompletadoInicio != null && ProjectUtils.esFechaValida(fechaCompletadoInicio,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date inicio = ProjectUtils.parseStringToDate(fechaCompletadoInicio,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    filters.put(Demanda.P_FECHA_COMPLETADO_INICIO, inicio);
                }
                if (fechaCompletadoFin != null && ProjectUtils.esFechaValida(fechaCompletadoFin,
                        ProjectConstants.Formato.FECHA_DD_MM_YYYY)) {
                    Date fin = ProjectUtils.parseStringToDate(fechaCompletadoFin,
                            ProjectConstants.Formato.FECHA_DD_MM_YYYY);
                    fin = ProjectUtils.sumarRestarSegundos(fin, 86399);
                    filters.put(Demanda.P_FECHA_COMPLETADO_FIN, fin);
                }
            } catch (Exception ex) { }

            boolean tieneRangoFechas = filters.containsKey(Demanda.P_FECHA_COMPLETADO_INICIO)
                    && filters.containsKey(Demanda.P_FECHA_COMPLETADO_FIN);
            if (tieneRangoFechas) {
                if (estadosFiltro == null || estadosFiltro.isEmpty()) {
                    filters.put(Demanda.P_ESTADO_IDS, List.of("C", "P"));
                } else {
                    List<String> estadosSinB = estadosFiltro.stream().filter(e -> !"B".equalsIgnoreCase(e)).toList();
                    if (estadosSinB.size() > 1) { filters.put(Demanda.P_ESTADO_IDS, estadosSinB); }
                    else if (estadosSinB.size() == 1) { filters.put(Demanda.P_ESTADO_ID, estadosSinB.get(0)); }
                }
            } else {
                if (estadosFiltro != null && !estadosFiltro.isEmpty()) {
                    if (estadosFiltro.size() > 1) { filters.put(Demanda.P_ESTADO_IDS, estadosFiltro); }
                    else { filters.put(Demanda.P_ESTADO_ID, estadosFiltro.get(0)); }
                }
            }

            List<Demanda> demandas = gestionDemandaUseCasePort.buscarDemandas(cuo, filters);

            SXSSFWorkbook wb = new SXSSFWorkbook(100);

            // Estilos: encabezados con color, negritas y mayúsculas; celdas con bordes
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Hoja Demandas (maestro)
            Sheet shDemandas = wb.createSheet("Demandas");
            String[] colsDemandas = new String[] {
                "id","sumilla","tipoRecepcion","fechaRecepcion","fechaCompletado",
                "idEstadoDemanda","estadoDemanda","idTipoPresentacion","tipoPresentacion",
                "idUsuario","usuarioDemanda","idUsuarioRecepcion","usuarioRecepcion","activo"
            };
            Row hdrDem = shDemandas.createRow(0);
            for (int i = 0; i < colsDemandas.length; i++) { Cell hc = hdrDem.createCell(i); hc.setCellValue(colsDemandas[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rDem = 1;

            // Hojas hijas
            Sheet shDemandantes = wb.createSheet("Demandantes");
            String[] colsDemandantes = new String[] { "demandaId","id","tipoDocumento","numeroDocumento","razonSocial","genero","fechaNacimiento","departamento","provincia","distrito","tipoDomicilio","domicilio","referencia","correo","celular","casillaElectronica","apoderadoComun","archivoUrl","activo" };
            Row hdrDemtes = shDemandantes.createRow(0);
            for (int i = 0; i < colsDemandantes.length; i++) { Cell hc = hdrDemtes.createCell(i); hc.setCellValue(colsDemandantes[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rDemtes = 1;

            Sheet shDemandados = wb.createSheet("Demandados");
            String[] colsDemandados = new String[] { "demandaId","id","tipoDocumento","numeroDocumento","razonSocial","departamento","provincia","distrito","tipoDomicilio","domicilio","referencia","activo" };
            Row hdrDems = shDemandados.createRow(0);
            for (int i = 0; i < colsDemandados.length; i++) { Cell hc = hdrDems.createCell(i); hc.setCellValue(colsDemandados[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rDems = 1;

            Sheet shPetitorios = wb.createSheet("Petitorios");
            String[] colsPetitorios = new String[] { "demandaId","id","tipo","pretensionPrincipal","concepto","pretensionAccesoria","monto","justificacion","fechaInicio","fechaFin","activo" };
            Row hdrPets = shPetitorios.createRow(0);
            for (int i = 0; i < colsPetitorios.length; i++) { Cell hc = hdrPets.createCell(i); hc.setCellValue(colsPetitorios[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rPets = 1;

            Sheet shRelLab = wb.createSheet("RelacionLaboral");
            String[] colsRelLab = new String[] { "demandaId","id","regimen","fechaInicio","fechaFin","anios","meses","dias","remuneracion","activo" };
            Row hdrRel = shRelLab.createRow(0);
            for (int i = 0; i < colsRelLab.length; i++) { Cell hc = hdrRel.createCell(i); hc.setCellValue(colsRelLab[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rRel = 1;

            Sheet shFund = wb.createSheet("Fundamentaciones");
            String[] colsFund = new String[] { "demandaId","id","contenido","activo" };
            Row hdrFund = shFund.createRow(0);
            for (int i = 0; i < colsFund.length; i++) { Cell hc = hdrFund.createCell(i); hc.setCellValue(colsFund[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rFund = 1;

            Sheet shFirm = wb.createSheet("Firmas");
            String[] colsFirm = new String[] { "demandaId","id","tipo","archivoUrl","activo" };
            Row hdrFirm = shFirm.createRow(0);
            for (int i = 0; i < colsFirm.length; i++) { Cell hc = hdrFirm.createCell(i); hc.setCellValue(colsFirm[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rFirm = 1;

            Sheet shAnex = wb.createSheet("Anexos");
            String[] colsAnex = new String[] { "demandaId","id","tipo","incluido","activo" };
            Row hdrAnex = shAnex.createRow(0);
            for (int i = 0; i < colsAnex.length; i++) { Cell hc = hdrAnex.createCell(i); hc.setCellValue(colsAnex[i].toUpperCase()); hc.setCellStyle(headerStyle); }
            int rAnex = 1;

            // Llenado
            for (Demanda d : demandas) {
                Row r = shDemandas.createRow(rDem++);
                int c = 0;
                Cell cell;
                cell = r.createCell(c++); cell.setCellValue(d.getId() != null ? d.getId() : 0); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getSumilla())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getTipoRecepcion())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getFechaRecepcion())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getFechaCompletado())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getIdEstadoDemanda())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getEstadoDemanda())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getIdTipoPresentacion())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getTipoPresentacion())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(d.getIdUsuario() != null ? d.getIdUsuario() : 0); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getUsuarioDemanda())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(d.getIdUsuarioRecepcion() != null ? d.getIdUsuarioRecepcion() : 0); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getUsuarioRecepcion())); cell.setCellStyle(dataStyle);
                cell = r.createCell(c++); cell.setCellValue(safeStr(d.getActivo())); cell.setCellStyle(dataStyle);

                Integer demandaId = d.getId() != null ? d.getId() : 0;
                if (d.getDemandantes() != null) {
                    for (var demte : d.getDemandantes()) {
                        Row rr = shDemandantes.createRow(rDemtes++);
                        int cc = 0;
                        Cell crr;
                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(demte.getId() != null ? demte.getId() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getTipoDocumento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getNumeroDocumento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getRazonSocial())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getGenero())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getFechaNacimiento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getDepartamento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getProvincia())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getDistrito())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getTipoDomicilio())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getDomicilio())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getReferencia())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getCorreo())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getCelular())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getCasillaElectronica())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getApoderadoComun())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getArchivoUrl())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getActivo())); crr.setCellStyle(dataStyle);
                    }
                }
                if (d.getDemandados() != null) {
                    for (var dems : d.getDemandados()) {
                        Row rr = shDemandados.createRow(rDems++);
                        int cc = 0;
                        Cell crr;
                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(dems.getId() != null ? dems.getId() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getTipoDocumento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getNumeroDocumento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getRazonSocial())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getDepartamento())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getProvincia())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getDistrito())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getTipoDomicilio())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getDomicilio())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getReferencia())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getActivo())); crr.setCellStyle(dataStyle);
                    }
                }
                if (d.getPetitorios() != null) {
                    for (var pet : d.getPetitorios()) {
                        Row rr = shPetitorios.createRow(rPets++);
                        int cc = 0;
                        Cell crr;
                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(pet.getId() != null ? pet.getId() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getTipo())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getPretensionPrincipal())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getConcepto())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getPretensionAccesoria())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(pet.getMonto() != null ? pet.getMonto().doubleValue() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getJustificacion())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getFechaInicio())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getFechaFin())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(pet.getActivo())); crr.setCellStyle(dataStyle);
                    }
                }
                if (d.getRelacionLaboral() != null) {
                    var rl = d.getRelacionLaboral();
                    Row rr = shRelLab.createRow(rRel++);
                    int cc = 0;
                    Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(rl.getId() != null ? rl.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(rl.getRegimen())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(rl.getFechaInicio())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(rl.getFechaFin())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(rl.getAnios() != null ? rl.getAnios() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(rl.getMeses() != null ? rl.getMeses() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(rl.getDias() != null ? rl.getDias() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(rl.getRemuneracion() != null ? rl.getRemuneracion().doubleValue() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(rl.getActivo())); crr.setCellStyle(dataStyle);
                }
                if (d.getFundamentaciones() != null) {
                    for (var fu : d.getFundamentaciones()) {
                        Row rr = shFund.createRow(rFund++);
                        int cc = 0;
                        Cell crr;
                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(fu.getId() != null ? fu.getId() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(fu.getContenido())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(fu.getActivo())); crr.setCellStyle(dataStyle);
                    }
                }
                if (d.getFirmas() != null) {
                    for (var fi : d.getFirmas()) {
                        Row rr = shFirm.createRow(rFirm++);
                        int cc = 0;
                        Cell crr;
                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(fi.getId() != null ? fi.getId() : 0); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getTipo())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getArchivoUrl())); crr.setCellStyle(dataStyle);
                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getActivo())); crr.setCellStyle(dataStyle);
                    }
                }
//                if (d.getAnexos() != null) {
//                    for (var an : d.getAnexos()) {
//                        Row rr = shAnex.createRow(rAnex++);
//                        int cc = 0;
//                        Cell crr;
//                        crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
//                        crr = rr.createCell(cc++); crr.setCellValue(an.getId() != null ? an.getId() : 0); crr.setCellStyle(dataStyle);
//                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(an.getTipo())); crr.setCellStyle(dataStyle);
//                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(an.getIncluido())); crr.setCellStyle(dataStyle);
//                        crr = rr.createCell(cc++); crr.setCellValue(safeStr(an.getActivo())); crr.setCellStyle(dataStyle);
//                    }
//                }
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            wb.write(baos);
            wb.dispose();
            wb.close();

            String filename = "demandas_multisheet_" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        } catch (ErrorException e) {
            GlobalResponse res = new GlobalResponse();
            res.setCodigoOperacion(cuo);
            handleException(cuo, e, res);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new byte[0]);
        } catch (Exception e) {
            GlobalResponse res = new GlobalResponse();
            res.setCodigoOperacion(cuo);
            handleException(cuo,
                    new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
                            String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_CONSULTAR.getNombre()),
                            e.getMessage(), e.getCause()),
                    res);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
        }
    }


	@Override
	public ResponseEntity<GlobalResponse> registrarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, DemandaRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			long inicio = System.currentTimeMillis();
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Demanda demandaDto = demandaMapper.toDemanda(request);
			gestionDemandaUseCasePort.registrarDemanda(cuo, demandaDto);
			res.setData(demandaDto);
			long fin = System.currentTimeMillis();
			AuditoriaAplicativos auditoriaAplicativos = auditoriaGeneralMapper.toAuditoriaAplicativos(
					request.getAuditoria(), cuo, ips, usuauth, uri, params, herramienta, res.getCodigo(),
					res.getDescripcion(), fin - inicio);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(request);
			auditoriaAplicativos.setPeticionBody(jsonString);
			auditoriaGeneralUseCasePort.crear(cuo, auditoriaAplicativos);
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_REGISTRAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType
				.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(request.getFormatoRespuesta())
						? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> actualizarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, Integer id, DemandaRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			long inicio = System.currentTimeMillis();
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			Demanda demandaDto = demandaMapper.toDemanda(request);
			demandaDto.setId(id);
			gestionDemandaUseCasePort.actualizarDemanda(cuo, demandaDto);
			res.setData(demandaDto);
			long fin = System.currentTimeMillis();
			AuditoriaAplicativos auditoriaAplicativos = auditoriaGeneralMapper.toAuditoriaAplicativos(
					request.getAuditoria(), cuo, ips, usuauth, uri, params, herramienta, res.getCodigo(),
					res.getDescripcion(), fin - inicio);
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(request);
			auditoriaAplicativos.setPeticionBody(jsonString);
			auditoriaGeneralUseCasePort.crear(cuo, auditoriaAplicativos);
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType
				.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(request.getFormatoRespuesta())
						? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> eliminarDemanda(String cuo, String ips, String usuauth, String uri,
			String params, String herramienta, String ip, Integer id, String formatoRespuesta) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
			gestionDemandaUseCasePort.eliminar(cuo, id);
			res.setData("Demanda eliminada exitosamente");
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ELIMINAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.XML.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_XML_VALUE
						: MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> actualizarEstadoRecepcionDemanda(String cuo, String ips, String usuauth,
			String uri, String params, String herramienta, String ip, Integer id, DemandaRecepcionRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			// Validación simple de estado solicitado
			if (request.getNuevoEstadoDemanda() == null || !"P".equals(request.getNuevoEstadoDemanda())) {
				throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(), String
						.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.ESTADO_ACTUALIZAR.getNombre()));
			}

			// Usar siempre la fecha actual del servidor
			Date fecha = new Date();

			gestionDemandaUseCasePort.actualizarCamposDemanda(cuo, id, request.getNuevoEstadoDemanda(),
					request.getTipoRecepcion(), fecha, request.getIdUsuarioRecepcion());

			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion("Demanda actualizada correctamente");
			Map<String, Object> data = new HashMap<>();
			data.put("idDemanda", id);
			data.put("estadoDemanda", request.getNuevoEstadoDemanda());
			data.put("tipoRecepcion", request.getTipoRecepcion());
			data.put("fechaRecepcion",
					ProjectUtils.convertDateToString(fecha, ProjectConstants.Formato.FECHA_DD_MM_YYYY));
			data.put("idUsuarioRecepcion", request.getIdUsuarioRecepcion());
			res.setData(data);
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}
}

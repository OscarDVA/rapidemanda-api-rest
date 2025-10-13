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

    @Override
    public ResponseEntity<byte[]> exportarDemandasExcel(String cuo, String ips, String usuauth, String uri,
            String params, String herramienta, String ip, Integer id, String bEstadoId, Integer idUsuario,
            String idTipoPresentacion, String tipoRecepcion, Integer idUsuarioRecepcion,
            String fechaCompletadoInicio, String fechaCompletadoFin) {
        try {
            Map<String, Object> filters = new HashMap<>();
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
                    fin = ProjectUtils.sumarRestarSegundos(fin, 86399);
                    filters.put(Demanda.P_FECHA_COMPLETADO_FIN, fin);
                }
            } catch (Exception ex) {
                // Ignorar errores de parseo y no aplicar filtros de fecha
            }

            boolean tieneRangoFechas = filters.containsKey(Demanda.P_FECHA_COMPLETADO_INICIO)
                    && filters.containsKey(Demanda.P_FECHA_COMPLETADO_FIN);

            if (tieneRangoFechas) {
                if (estadosFiltro == null || estadosFiltro.isEmpty()) {
                    filters.put(Demanda.P_ESTADO_IDS, List.of("C", "P"));
                } else {
                    List<String> estadosSinB = estadosFiltro.stream()
                            .filter(e -> !"B".equalsIgnoreCase(e))
                            .toList();
                    if (estadosSinB.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosSinB);
                    } else if (estadosSinB.size() == 1) {
                        filters.put(Demanda.P_ESTADO_ID, estadosSinB.get(0));
                    }
                }
            } else {
                if (estadosFiltro != null && !estadosFiltro.isEmpty()) {
                    if (estadosFiltro.size() > 1) {
                        filters.put(Demanda.P_ESTADO_IDS, estadosFiltro);
                    } else {
                        filters.put(Demanda.P_ESTADO_ID, estadosFiltro.get(0));
                    }
                }
            }

            List<Demanda> demandas = gestionDemandaUseCasePort.buscarDemandas(cuo, filters);

            SXSSFWorkbook wb = new SXSSFWorkbook(100);
            Sheet sheet = wb.createSheet("Demandas");

            // Encabezados
            Row header = sheet.createRow(0);
            String[] cols = new String[] {
                "id","sumilla","tipoRecepcion","fechaRecepcion","fechaCompletado",
                "idEstadoDemanda","estadoDemanda","idTipoPresentacion","tipoPresentacion",
                "idUsuario","usuarioDemanda","idUsuarioRecepcion","usuarioRecepcion","activo",
                "demandantes","demandados","petitorios","relacionLaboral","fundamentaciones","firmas","anexos"
            };
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
            }

            ObjectMapper om = new ObjectMapper();
            int rowIdx = 1;
            for (Demanda d : demandas) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(d.getId() != null ? d.getId() : 0);
                row.createCell(col++).setCellValue(safeStr(d.getSumilla()));
                row.createCell(col++).setCellValue(safeStr(d.getTipoRecepcion()));
                row.createCell(col++).setCellValue(safeStr(d.getFechaRecepcion()));
                row.createCell(col++).setCellValue(safeStr(d.getFechaCompletado()));
                row.createCell(col++).setCellValue(safeStr(d.getIdEstadoDemanda()));
                row.createCell(col++).setCellValue(safeStr(d.getEstadoDemanda()));
                row.createCell(col++).setCellValue(safeStr(d.getIdTipoPresentacion()));
                row.createCell(col++).setCellValue(safeStr(d.getTipoPresentacion()));
                row.createCell(col++).setCellValue(d.getIdUsuario() != null ? d.getIdUsuario() : 0);
                row.createCell(col++).setCellValue(safeStr(d.getUsuarioDemanda()));
                row.createCell(col++).setCellValue(d.getIdUsuarioRecepcion() != null ? d.getIdUsuarioRecepcion() : 0);
                row.createCell(col++).setCellValue(safeStr(d.getUsuarioRecepcion()));
                row.createCell(col++).setCellValue(safeStr(d.getActivo()));
                row.createCell(col++).setCellValue(safeJson(om, d.getDemandantes()));
                row.createCell(col++).setCellValue(safeJson(om, d.getDemandados()));
                row.createCell(col++).setCellValue(safeJson(om, d.getPetitorios()));
                row.createCell(col++).setCellValue(safeJson(om, d.getRelacionLaboral()));
                row.createCell(col++).setCellValue(safeJson(om, d.getFundamentaciones()));
                row.createCell(col++).setCellValue(safeJson(om, d.getFirmas()));
                row.createCell(col++).setCellValue(safeJson(om, d.getAnexos()));
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            wb.write(baos);
            wb.dispose();
            wb.close();

            String filename = "demandas_" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
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

    private String safeStr(String s) { return s != null ? s : ""; }
    private String safeJson(ObjectMapper om, Object obj) {
        try { return obj != null ? om.writeValueAsString(obj) : ""; } catch (Exception ex) { return ""; }
    }

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

            // Hoja Demandas (maestro)
            Sheet shDemandas = wb.createSheet("Demandas");
            String[] colsDemandas = new String[] {
                "id","sumilla","tipoRecepcion","fechaRecepcion","fechaCompletado",
                "idEstadoDemanda","estadoDemanda","idTipoPresentacion","tipoPresentacion",
                "idUsuario","usuarioDemanda","idUsuarioRecepcion","usuarioRecepcion","activo"
            };
            Row hdrDem = shDemandas.createRow(0);
            for (int i = 0; i < colsDemandas.length; i++) { hdrDem.createCell(i).setCellValue(colsDemandas[i]); }
            int rDem = 1;

            // Hojas hijas
            Sheet shDemandantes = wb.createSheet("Demandantes");
            String[] colsDemandantes = new String[] { "demandaId","id","tipoDocumento","numeroDocumento","razonSocial","genero","fechaNacimiento","departamento","provincia","distrito","tipoDomicilio","domicilio","referencia","correo","celular","casillaElectronica","apoderadoComun","archivoUrl","activo" };
            Row hdrDemtes = shDemandantes.createRow(0);
            for (int i = 0; i < colsDemandantes.length; i++) { hdrDemtes.createCell(i).setCellValue(colsDemandantes[i]); }
            int rDemtes = 1;

            Sheet shDemandados = wb.createSheet("Demandados");
            String[] colsDemandados = new String[] { "demandaId","id","tipoDocumento","numeroDocumento","razonSocial","departamento","provincia","distrito","tipoDomicilio","domicilio","referencia","activo" };
            Row hdrDems = shDemandados.createRow(0);
            for (int i = 0; i < colsDemandados.length; i++) { hdrDems.createCell(i).setCellValue(colsDemandados[i]); }
            int rDems = 1;

            Sheet shPetitorios = wb.createSheet("Petitorios");
            String[] colsPetitorios = new String[] { "demandaId","id","tipo","pretensionPrincipal","concepto","pretensionAccesoria","monto","justificacion","fechaInicio","fechaFin","activo" };
            Row hdrPets = shPetitorios.createRow(0);
            for (int i = 0; i < colsPetitorios.length; i++) { hdrPets.createCell(i).setCellValue(colsPetitorios[i]); }
            int rPets = 1;

            Sheet shRelLab = wb.createSheet("RelacionLaboral");
            String[] colsRelLab = new String[] { "demandaId","id","regimen","fechaInicio","fechaFin","anios","meses","dias","remuneracion","activo" };
            Row hdrRel = shRelLab.createRow(0);
            for (int i = 0; i < colsRelLab.length; i++) { hdrRel.createCell(i).setCellValue(colsRelLab[i]); }
            int rRel = 1;

            Sheet shFund = wb.createSheet("Fundamentaciones");
            String[] colsFund = new String[] { "demandaId","id","contenido","activo" };
            Row hdrFund = shFund.createRow(0);
            for (int i = 0; i < colsFund.length; i++) { hdrFund.createCell(i).setCellValue(colsFund[i]); }
            int rFund = 1;

            Sheet shFirm = wb.createSheet("Firmas");
            String[] colsFirm = new String[] { "demandaId","id","tipo","archivoUrl","activo" };
            Row hdrFirm = shFirm.createRow(0);
            for (int i = 0; i < colsFirm.length; i++) { hdrFirm.createCell(i).setCellValue(colsFirm[i]); }
            int rFirm = 1;

            Sheet shAnex = wb.createSheet("Anexos");
            String[] colsAnex = new String[] { "demandaId","id","tipo","incluido","activo" };
            Row hdrAnex = shAnex.createRow(0);
            for (int i = 0; i < colsAnex.length; i++) { hdrAnex.createCell(i).setCellValue(colsAnex[i]); }
            int rAnex = 1;

            // Llenado
            for (Demanda d : demandas) {
                Row r = shDemandas.createRow(rDem++);
                int c = 0;
                r.createCell(c++).setCellValue(d.getId() != null ? d.getId() : 0);
                r.createCell(c++).setCellValue(safeStr(d.getSumilla()));
                r.createCell(c++).setCellValue(safeStr(d.getTipoRecepcion()));
                r.createCell(c++).setCellValue(safeStr(d.getFechaRecepcion()));
                r.createCell(c++).setCellValue(safeStr(d.getFechaCompletado()));
                r.createCell(c++).setCellValue(safeStr(d.getIdEstadoDemanda()));
                r.createCell(c++).setCellValue(safeStr(d.getEstadoDemanda()));
                r.createCell(c++).setCellValue(safeStr(d.getIdTipoPresentacion()));
                r.createCell(c++).setCellValue(safeStr(d.getTipoPresentacion()));
                r.createCell(c++).setCellValue(d.getIdUsuario() != null ? d.getIdUsuario() : 0);
                r.createCell(c++).setCellValue(safeStr(d.getUsuarioDemanda()));
                r.createCell(c++).setCellValue(d.getIdUsuarioRecepcion() != null ? d.getIdUsuarioRecepcion() : 0);
                r.createCell(c++).setCellValue(safeStr(d.getUsuarioRecepcion()));
                r.createCell(c++).setCellValue(safeStr(d.getActivo()));

                Integer demandaId = d.getId() != null ? d.getId() : 0;
                if (d.getDemandantes() != null) {
                    for (var demte : d.getDemandantes()) {
                        Row rr = shDemandantes.createRow(rDemtes++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(demte.getId() != null ? demte.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(demte.getTipoDocumento()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getNumeroDocumento()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getRazonSocial()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getGenero()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getFechaNacimiento()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getDepartamento()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getProvincia()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getDistrito()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getTipoDomicilio()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getDomicilio()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getReferencia()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getCorreo()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getCelular()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getCasillaElectronica()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getApoderadoComun()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getArchivoUrl()));
                        rr.createCell(cc++).setCellValue(safeStr(demte.getActivo()));
                    }
                }
                if (d.getDemandados() != null) {
                    for (var dems : d.getDemandados()) {
                        Row rr = shDemandados.createRow(rDems++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(dems.getId() != null ? dems.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(dems.getTipoDocumento()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getNumeroDocumento()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getRazonSocial()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getDepartamento()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getProvincia()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getDistrito()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getTipoDomicilio()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getDomicilio()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getReferencia()));
                        rr.createCell(cc++).setCellValue(safeStr(dems.getActivo()));
                    }
                }
                if (d.getPetitorios() != null) {
                    for (var pet : d.getPetitorios()) {
                        Row rr = shPetitorios.createRow(rPets++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(pet.getId() != null ? pet.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(pet.getTipo()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getPretensionPrincipal()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getConcepto()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getPretensionAccesoria()));
                        rr.createCell(cc++).setCellValue(pet.getMonto() != null ? pet.getMonto().doubleValue() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(pet.getJustificacion()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getFechaInicio()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getFechaFin()));
                        rr.createCell(cc++).setCellValue(safeStr(pet.getActivo()));
                    }
                }
                if (d.getRelacionLaboral() != null) {
                    var rl = d.getRelacionLaboral();
                    Row rr = shRelLab.createRow(rRel++);
                    int cc = 0;
                    rr.createCell(cc++).setCellValue(demandaId);
                    rr.createCell(cc++).setCellValue(rl.getId() != null ? rl.getId() : 0);
                    rr.createCell(cc++).setCellValue(safeStr(rl.getRegimen()));
                    rr.createCell(cc++).setCellValue(safeStr(rl.getFechaInicio()));
                    rr.createCell(cc++).setCellValue(safeStr(rl.getFechaFin()));
                    rr.createCell(cc++).setCellValue(rl.getAnios() != null ? rl.getAnios() : 0);
                    rr.createCell(cc++).setCellValue(rl.getMeses() != null ? rl.getMeses() : 0);
                    rr.createCell(cc++).setCellValue(rl.getDias() != null ? rl.getDias() : 0);
                    rr.createCell(cc++).setCellValue(rl.getRemuneracion() != null ? rl.getRemuneracion().doubleValue() : 0);
                    rr.createCell(cc++).setCellValue(safeStr(rl.getActivo()));
                }
                if (d.getFundamentaciones() != null) {
                    for (var fu : d.getFundamentaciones()) {
                        Row rr = shFund.createRow(rFund++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(fu.getId() != null ? fu.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(fu.getContenido()));
                        rr.createCell(cc++).setCellValue(safeStr(fu.getActivo()));
                    }
                }
                if (d.getFirmas() != null) {
                    for (var fi : d.getFirmas()) {
                        Row rr = shFirm.createRow(rFirm++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(fi.getId() != null ? fi.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(fi.getTipo()));
                        rr.createCell(cc++).setCellValue(safeStr(fi.getArchivoUrl()));
                        rr.createCell(cc++).setCellValue(safeStr(fi.getActivo()));
                    }
                }
                if (d.getAnexos() != null) {
                    for (var an : d.getAnexos()) {
                        Row rr = shAnex.createRow(rAnex++);
                        int cc = 0;
                        rr.createCell(cc++).setCellValue(demandaId);
                        rr.createCell(cc++).setCellValue(an.getId() != null ? an.getId() : 0);
                        rr.createCell(cc++).setCellValue(safeStr(an.getTipo()));
                        rr.createCell(cc++).setCellValue(safeStr(an.getIncluido()));
                        rr.createCell(cc++).setCellValue(safeStr(an.getActivo()));
                    }
                }
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
    public ResponseEntity<byte[]> exportarDemandasExcelAplanado(String cuo, String ips, String usuauth, String uri,
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
            Sheet sheet = wb.createSheet("DemandasFlat");
            String[] cols = new String[] {
                // Datos demanda
                "id","sumilla","tipoRecepcion","fechaRecepcion","fechaCompletado",
                "idEstadoDemanda","estadoDemanda","idTipoPresentacion","tipoPresentacion",
                "idUsuario","usuarioDemanda","idUsuarioRecepcion","usuarioRecepcion","activo",
                // Demandado (fila base de repetición)
                "dems_id","dems_tipoDocumento","dems_numeroDocumento","dems_razonSocial","dems_departamento","dems_provincia","dems_distrito","dems_tipoDomicilio","dems_domicilio","dems_referencia","dems_activo",
                // Agregados de otras relaciones
                "demandantes_txt","petitorios_txt","fundamentaciones_txt","firmas_txt","anexos_txt","relacionLaboral_txt"
            };
            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) { header.createCell(i).setCellValue(cols[i]); }

            int rowIdx = 1;
            for (Demanda d : demandas) {
                List<pe.gob.pj.rapidemanda.domain.model.servicio.Demandado> base = (d.getDemandados() != null && !d.getDemandados().isEmpty()) ? d.getDemandados() : List.of(new pe.gob.pj.rapidemanda.domain.model.servicio.Demandado());
                String demandantesTxt = (d.getDemandantes() != null) ? d.getDemandantes().stream().map(x -> safeStr(x.getNumeroDocumento()) + " - " + safeStr(x.getRazonSocial())).reduce((a,b) -> a+"; "+b).orElse("") : "";
                String petitoriosTxt = (d.getPetitorios() != null) ? d.getPetitorios().stream().map(x -> safeStr(x.getTipo()) + ": " + safeStr(x.getConcepto())).reduce((a,b) -> a+"; "+b).orElse("") : "";
                String fundTxt = (d.getFundamentaciones() != null) ? d.getFundamentaciones().stream().map(x -> safeStr(x.getContenido())).reduce((a,b) -> a+"; "+b).orElse("") : "";
                String firmasTxt = (d.getFirmas() != null) ? d.getFirmas().stream().map(x -> safeStr(x.getTipo()) + "@" + safeStr(x.getArchivoUrl())).reduce((a,b) -> a+"; "+b).orElse("") : "";
                String anexosTxt = (d.getAnexos() != null) ? d.getAnexos().stream().map(x -> safeStr(x.getTipo()) + "=" + safeStr(x.getIncluido())).reduce((a,b) -> a+"; "+b).orElse("") : "";
                String relLabTxt = (d.getRelacionLaboral() != null) ? (safeStr(d.getRelacionLaboral().getRegimen()) + ", " + safeStr(d.getRelacionLaboral().getFechaInicio()) + "-" + safeStr(d.getRelacionLaboral().getFechaFin())) : "";

                for (var dems : base) {
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    row.createCell(col++).setCellValue(d.getId() != null ? d.getId() : 0);
                    row.createCell(col++).setCellValue(safeStr(d.getSumilla()));
                    row.createCell(col++).setCellValue(safeStr(d.getTipoRecepcion()));
                    row.createCell(col++).setCellValue(safeStr(d.getFechaRecepcion()));
                    row.createCell(col++).setCellValue(safeStr(d.getFechaCompletado()));
                    row.createCell(col++).setCellValue(safeStr(d.getIdEstadoDemanda()));
                    row.createCell(col++).setCellValue(safeStr(d.getEstadoDemanda()));
                    row.createCell(col++).setCellValue(safeStr(d.getIdTipoPresentacion()));
                    row.createCell(col++).setCellValue(safeStr(d.getTipoPresentacion()));
                    row.createCell(col++).setCellValue(d.getIdUsuario() != null ? d.getIdUsuario() : 0);
                    row.createCell(col++).setCellValue(safeStr(d.getUsuarioDemanda()));
                    row.createCell(col++).setCellValue(d.getIdUsuarioRecepcion() != null ? d.getIdUsuarioRecepcion() : 0);
                    row.createCell(col++).setCellValue(safeStr(d.getUsuarioRecepcion()));
                    row.createCell(col++).setCellValue(safeStr(d.getActivo()));

                    row.createCell(col++).setCellValue(dems.getId() != null ? dems.getId() : 0);
                    row.createCell(col++).setCellValue(safeStr(dems.getTipoDocumento()));
                    row.createCell(col++).setCellValue(safeStr(dems.getNumeroDocumento()));
                    row.createCell(col++).setCellValue(safeStr(dems.getRazonSocial()));
                    row.createCell(col++).setCellValue(safeStr(dems.getDepartamento()));
                    row.createCell(col++).setCellValue(safeStr(dems.getProvincia()));
                    row.createCell(col++).setCellValue(safeStr(dems.getDistrito()));
                    row.createCell(col++).setCellValue(safeStr(dems.getTipoDomicilio()));
                    row.createCell(col++).setCellValue(safeStr(dems.getDomicilio()));
                    row.createCell(col++).setCellValue(safeStr(dems.getReferencia()));
                    row.createCell(col++).setCellValue(safeStr(dems.getActivo()));

                    row.createCell(col++).setCellValue(demandantesTxt);
                    row.createCell(col++).setCellValue(petitoriosTxt);
                    row.createCell(col++).setCellValue(fundTxt);
                    row.createCell(col++).setCellValue(firmasTxt);
                    row.createCell(col++).setCellValue(anexosTxt);
                    row.createCell(col++).setCellValue(relLabTxt);
                }
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            wb.write(baos);
            wb.dispose();
            wb.close();

            String filename = "demandas_flat_" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
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

package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;
import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;
import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReporteExcelPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionConceptoUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDepartamentoUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDistritoUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPetitorioUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionAccesoriaUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionPrincipalUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionProvinciaUseCasePort;

@Component("gestionReporteExcelPersistencePort")
public class GestionReporteExcelPersistenceAdapter implements GestionReporteExcelPersistencePort {

    @Autowired
    @Qualifier("gestionDemandaPersistencePort")
    private GestionDemandaPersistencePort gestionDemandaPersistencePort;

    @Autowired
    @Qualifier("gestionDepartamentoUseCasePort")
    private GestionDepartamentoUseCasePort gestionDepartamentoUseCasePort;

    @Autowired
    @Qualifier("gestionProvinciaUseCasePort")
    private GestionProvinciaUseCasePort gestionProvinciaUseCasePort;

    @Autowired
    @Qualifier("gestionDistritoUseCasePort")
    private GestionDistritoUseCasePort gestionDistritoUseCasePort;

    @Autowired
    @Qualifier("gestionPetitorioUseCasePort")
    private GestionPetitorioUseCasePort gestionPetitorioUseCasePort;

    @Autowired
    @Qualifier("gestionPretensionPrincipalUseCasePort")
    private GestionPretensionPrincipalUseCasePort gestionPretensionPrincipalUseCasePort;

    @Autowired
    @Qualifier("gestionConceptoUseCasePort")
    private GestionConceptoUseCasePort gestionConceptoUseCasePort;

    @Autowired
    @Qualifier("gestionPretensionAccesoriaUseCasePort")
    private GestionPretensionAccesoriaUseCasePort gestionPretensionAccesoriaUseCasePort;

    private final java.util.concurrent.ConcurrentHashMap<String, String> cacheDepartamentos = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cacheProvincias = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cacheDistritos = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cachePetitorios = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cachePretensionesPrincipales = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cacheConceptos = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, String> cachePretensionesAccesorias = new java.util.concurrent.ConcurrentHashMap<>();

    private String safeStr(String s) { return s != null ? s : ""; }

    private void precargarUbigeoYCatalogosSiNecesario(String cuo) {
        try {
            if (cacheDepartamentos.isEmpty()) {
                var deps = gestionDepartamentoUseCasePort.buscarDepartamento(cuo, java.util.Collections.emptyMap());
                for (Departamento d : deps) {
                    if (d.getId() != null && d.getNombre() != null) { cacheDepartamentos.putIfAbsent(d.getId(), d.getNombre()); }
                }
            }
            if (cacheProvincias.isEmpty()) {
                var provs = gestionProvinciaUseCasePort.buscarProvincia(cuo, java.util.Collections.emptyMap());
                for (Provincia p : provs) {
                    if (p.getId() != null && p.getNombre() != null) { cacheProvincias.putIfAbsent(p.getId(), p.getNombre()); }
                }
            }
            if (cacheDistritos.isEmpty()) {
                var dists = gestionDistritoUseCasePort.buscarDistrito(cuo, java.util.Collections.emptyMap());
                for (Distrito d : dists) {
                    if (d.getId() != null && d.getNombre() != null) { cacheDistritos.putIfAbsent(d.getId(), d.getNombre()); }
                }
            }
            if (cachePetitorios.isEmpty()) {
                var pets = gestionPetitorioUseCasePort.buscarPetitorio(cuo);
                for (CatalogoPetitorio p : pets) {
                    if (p.getId() != null && p.getNombre() != null) { cachePetitorios.putIfAbsent(String.valueOf(p.getId()), p.getNombre()); }
                }
            }
            if (cachePretensionesPrincipales.isEmpty()) {
                var pps = gestionPretensionPrincipalUseCasePort.buscarPretensionPrincipal(cuo, java.util.Collections.emptyMap());
                for (CatalogoPretensionPrincipal pp : pps) {
                    if (pp.getId() != null && pp.getNombre() != null) { cachePretensionesPrincipales.putIfAbsent(String.valueOf(pp.getId()), pp.getNombre()); }
                }
            }
            if (cacheConceptos.isEmpty()) {
                var concs = gestionConceptoUseCasePort.buscarConcepto(cuo, java.util.Collections.emptyMap());
                for (CatalogoConcepto c : concs) {
                    if (c.getId() != null && c.getNombre() != null) { cacheConceptos.putIfAbsent(String.valueOf(c.getId()), c.getNombre()); }
                }
            }
            if (cachePretensionesAccesorias.isEmpty()) {
                var accs = gestionPretensionAccesoriaUseCasePort.buscarPretensionAccesoria(cuo, java.util.Collections.emptyMap());
                for (CatalogoPretensionAccesoria pa : accs) {
                    if (pa.getId() != null && pa.getNombre() != null) { cachePretensionesAccesorias.putIfAbsent(String.valueOf(pa.getId()), pa.getNombre()); }
                }
            }
        } catch (Exception ignored) {}
    }

    private String nombreDepartamento(String cuo, String departamentoId) {
        if (departamentoId == null || departamentoId.isBlank()) return "";
        var nombre = cacheDepartamentos.get(departamentoId);
        if (nombre != null) return nombre;
        try {
            var lista = gestionDepartamentoUseCasePort.buscarDepartamento(cuo, java.util.Map.of(Departamento.P_DEPARTAMENTO_ID, departamentoId));
            if (!lista.isEmpty() && lista.get(0).getNombre() != null) {
                nombre = lista.get(0).getNombre();
                cacheDepartamentos.put(departamentoId, nombre);
                return nombre;
            }
        } catch (Exception ignored) {}
        return departamentoId;
    }

    private String nombreProvincia(String cuo, String provinciaId, String departamentoId) {
        if (provinciaId == null || provinciaId.isBlank()) return "";
        var nombre = cacheProvincias.get(provinciaId);
        if (nombre != null) return nombre;
        try {
            java.util.Map<String, Object> filters = (departamentoId == null || departamentoId.isBlank())
                    ? java.util.Map.of(Provincia.P_PROVINCIA_ID, provinciaId)
                    : java.util.Map.of(Provincia.P_PROVINCIA_ID, provinciaId, Provincia.P_DEPARTAMENTO_ID, departamentoId);
            var lista = gestionProvinciaUseCasePort.buscarProvincia(cuo, filters);
            if (!lista.isEmpty() && lista.get(0).getNombre() != null) {
                nombre = lista.get(0).getNombre();
                cacheProvincias.put(provinciaId, nombre);
                return nombre;
            }
        } catch (Exception ignored) {}
        return provinciaId;
    }

    private String nombreDistrito(String cuo, String distritoId, String provinciaId) {
        if (distritoId == null || distritoId.isBlank()) return "";
        var nombre = cacheDistritos.get(distritoId);
        if (nombre != null) return nombre;
        try {
            java.util.Map<String, Object> filters = (provinciaId == null || provinciaId.isBlank())
                    ? java.util.Map.of(Distrito.P_DISTRITO_ID, distritoId)
                    : java.util.Map.of(Distrito.P_DISTRITO_ID, distritoId, Distrito.P_PROVINCIA_ID, provinciaId);
            var lista = gestionDistritoUseCasePort.buscarDistrito(cuo, filters);
            if (!lista.isEmpty() && lista.get(0).getNombre() != null) {
                nombre = lista.get(0).getNombre();
                cacheDistritos.put(distritoId, nombre);
                return nombre;
            }
        } catch (Exception ignored) {}
        return distritoId;
    }

    private String nombrePetitorio(String cuo, String petitorioId) {
        if (petitorioId == null || petitorioId.isBlank()) return "";
        var nombre = cachePetitorios.get(petitorioId);
        if (nombre != null) return nombre;
        try {
            var lista = gestionPetitorioUseCasePort.buscarPetitorio(cuo);
            for (CatalogoPetitorio p : lista) {
                if (p.getId() != null && p.getNombre() != null) { cachePetitorios.putIfAbsent(String.valueOf(p.getId()), p.getNombre()); }
            }
            nombre = cachePetitorios.get(petitorioId);
            if (nombre != null) return nombre;
        } catch (Exception ignored) {}
        return petitorioId;
    }

    private String nombrePretensionPrincipal(String cuo, String pretensionPrincipalId) {
        if (pretensionPrincipalId == null || pretensionPrincipalId.isBlank()) return "";
        var nombre = cachePretensionesPrincipales.get(pretensionPrincipalId);
        if (nombre != null) return nombre;
        try {
            var lista = gestionPretensionPrincipalUseCasePort.buscarPretensionPrincipal(cuo, java.util.Collections.emptyMap());
            for (CatalogoPretensionPrincipal pp : lista) {
                if (pp.getId() != null && pp.getNombre() != null) { cachePretensionesPrincipales.putIfAbsent(String.valueOf(pp.getId()), pp.getNombre()); }
            }
            nombre = cachePretensionesPrincipales.get(pretensionPrincipalId);
            if (nombre != null) return nombre;
        } catch (Exception ignored) {}
        return pretensionPrincipalId;
    }

    private String nombreConcepto(String cuo, String conceptoId) {
        if (conceptoId == null || conceptoId.isBlank()) return "";
        var nombre = cacheConceptos.get(conceptoId);
        if (nombre != null) return nombre;
        try {
            var lista = gestionConceptoUseCasePort.buscarConcepto(cuo, java.util.Collections.emptyMap());
            for (CatalogoConcepto c : lista) {
                if (c.getId() != null && c.getNombre() != null) { cacheConceptos.putIfAbsent(String.valueOf(c.getId()), c.getNombre()); }
            }
            nombre = cacheConceptos.get(conceptoId);
            if (nombre != null) return nombre;
        } catch (Exception ignored) {}
        return conceptoId;
    }

    private String nombrePretensionAccesoria(String cuo, String accesoriaId) {
        if (accesoriaId == null || accesoriaId.isBlank()) return "";
        var nombre = cachePretensionesAccesorias.get(accesoriaId);
        if (nombre != null) return nombre;
        try {
            var lista = gestionPretensionAccesoriaUseCasePort.buscarPretensionAccesoria(cuo, java.util.Collections.emptyMap());
            for (CatalogoPretensionAccesoria pa : lista) {
                if (pa.getId() != null && pa.getNombre() != null) { cachePretensionesAccesorias.putIfAbsent(String.valueOf(pa.getId()), pa.getNombre()); }
            }
            nombre = cachePretensionesAccesorias.get(accesoriaId);
            if (nombre != null) return nombre;
        } catch (Exception ignored) {}
        return accesoriaId;
    }

    @Override
    public byte[] generarReporteDemandasExcelMultiHoja(String cuo, Map<String, Object> filters) throws Exception {
        // Precargar catálogos para normalización
        precargarUbigeoYCatalogosSiNecesario(cuo);

        List<Demanda> demandas = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);
        if (demandas == null) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_EXPORTAR_EXCEL.getNombre()));
        }

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
                    int cc = 0; Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(demte.getId() != null ? demte.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getTipoDocumento())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getNumeroDocumento())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getRazonSocial())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getGenero())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(demte.getFechaNacimiento())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreDepartamento(cuo, safeStr(demte.getDepartamento()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreProvincia(cuo, safeStr(demte.getProvincia()), safeStr(demte.getDepartamento()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreDistrito(cuo, safeStr(demte.getDistrito()), safeStr(demte.getProvincia()))); crr.setCellStyle(dataStyle);
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
                    int cc = 0; Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(dems.getId() != null ? dems.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getTipoDocumento())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getNumeroDocumento())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getRazonSocial())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreDepartamento(cuo, safeStr(dems.getDepartamento()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreProvincia(cuo, safeStr(dems.getProvincia()), safeStr(dems.getDepartamento()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreDistrito(cuo, safeStr(dems.getDistrito()), safeStr(dems.getProvincia()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getTipoDomicilio())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getDomicilio())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getReferencia())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(dems.getActivo())); crr.setCellStyle(dataStyle);
                }
            }
            if (d.getPetitorios() != null) {
                for (var pet : d.getPetitorios()) {
                    Row rr = shPetitorios.createRow(rPets++);
                    int cc = 0; Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(pet.getId() != null ? pet.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombrePetitorio(cuo, safeStr(pet.getTipo()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombrePretensionPrincipal(cuo, safeStr(pet.getPretensionPrincipal()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombreConcepto(cuo, safeStr(pet.getConcepto()))); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(nombrePretensionAccesoria(cuo, safeStr(pet.getPretensionAccesoria()))); crr.setCellStyle(dataStyle);
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
                int cc = 0; Cell crr;
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
                    int cc = 0; Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(fu.getId() != null ? fu.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(fu.getContenido())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(fu.getActivo())); crr.setCellStyle(dataStyle);
                }
            }
            if (d.getFirmas() != null) {
                for (var fi : d.getFirmas()) {
                    Row rr = shFirm.createRow(rFirm++);
                    int cc = 0; Cell crr;
                    crr = rr.createCell(cc++); crr.setCellValue(demandaId); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(fi.getId() != null ? fi.getId() : 0); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getTipo())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getArchivoUrl())); crr.setCellStyle(dataStyle);
                    crr = rr.createCell(cc++); crr.setCellValue(safeStr(fi.getActivo())); crr.setCellStyle(dataStyle);
                }
            }
        }

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        wb.write(baos);
        wb.dispose();
        wb.close();
        return baos.toByteArray();
    }
}
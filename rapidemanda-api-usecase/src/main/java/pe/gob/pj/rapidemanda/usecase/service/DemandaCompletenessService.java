package pe.gob.pj.rapidemanda.usecase.service;

import pe.gob.pj.rapidemanda.domain.model.servicio.*;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service("demandaCompletenessService")
public class DemandaCompletenessService {

    private static final int MAX_SUMILLA = 4000;
    private static final int MAX_TEXTO = 4000;
    private static final int MAX_URL = 1024;

    public DemandaCompletenessResult validar(Demanda demanda) {
        List<String> faltantes = new ArrayList<>();

        if (demanda == null) {
            faltantes.add("demanda");
            return new DemandaCompletenessResult(false, faltantes);
        }

        if (!ProjectUtils.tieneTamanioValido(demanda.getSumilla(), MAX_SUMILLA)) {
            faltantes.add("sumilla");
        }
        if (demanda.getIdTipoPresentacion() == null) {
            faltantes.add("tipoPresentacion");
        }
        String tipo = demanda.getIdTipoPresentacion();
        boolean esFisica = "F".equalsIgnoreCase(tipo);
        boolean esMesaVirtual = "M".equalsIgnoreCase(tipo);
        if (demanda.getPetitorios() == null || demanda.getPetitorios().isEmpty()) {
            faltantes.add("petitorios");
        } else {
            boolean petitorioValido = demanda.getPetitorios().stream().anyMatch(p ->
                    ProjectUtils.tieneTamanioValido(p.getPretensionPrincipal(), MAX_TEXTO) ||
                            ProjectUtils.tieneTamanioValido(p.getConcepto(), MAX_TEXTO) ||
                            (p.getMonto() != null)
            );
            if (!petitorioValido) {
                faltantes.add("petitoriosIncompletos");
            }
        }
        if (demanda.getDemandantes() == null || demanda.getDemandantes().isEmpty()) {
            faltantes.add("demandantes");
        } else if (esMesaVirtual) {
            boolean demandantesConArchivo = demanda.getDemandantes().stream()
                    .allMatch(d -> ProjectUtils.tieneTamanioValido(d.getArchivoUrl(), MAX_URL));
            if (!demandantesConArchivo) {
                faltantes.add("demandantesArchivoUrl");
            }
        }
        if (demanda.getDemandados() == null || demanda.getDemandados().isEmpty()) {
            faltantes.add("demandados");
        }
        if (demanda.getRelacionLaboral() != null) {
            RelacionLaboral rl = demanda.getRelacionLaboral();
            boolean regimenValido = ProjectUtils.tieneTamanioValido(rl.getRegimen(), 50);
            boolean remuneracionPresente = rl.getRemuneracion() != null;
            boolean tiempoOFechas =
                    ProjectUtils.esFechaValida(rl.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY) ||
                    ProjectUtils.esFechaValida(rl.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY) ||
                    rl.getAnios() != null || rl.getMeses() != null || rl.getDias() != null;

            boolean rlBasica = regimenValido && remuneracionPresente && tiempoOFechas;
            if (!rlBasica) {
                faltantes.add("relacionLaboralIncompleta");
            }
        }
        if (esMesaVirtual) {
            if (demanda.getFirmas() == null || demanda.getFirmas().isEmpty()) {
                faltantes.add("firmas");
            } else {
                boolean firmaValida = demanda.getFirmas().stream()
                        .anyMatch(f -> ProjectUtils.tieneTamanioValido(f.getArchivoUrl(), MAX_URL));
                if (!firmaValida) {
                    faltantes.add("firmasIncompletas");
                }
            }
        }

        boolean completa = faltantes.isEmpty();
        return new DemandaCompletenessResult(completa, faltantes);
    }

    public static class DemandaCompletenessResult {
        private final boolean completa;
        private final List<String> faltantes;

        public DemandaCompletenessResult(boolean completa, List<String> faltantes) {
            this.completa = completa;
            this.faltantes = Objects.requireNonNullElseGet(faltantes, ArrayList::new);
        }

        public boolean isCompleta() { return completa; }
        public List<String> getFaltantes() { return faltantes; }
    }
}
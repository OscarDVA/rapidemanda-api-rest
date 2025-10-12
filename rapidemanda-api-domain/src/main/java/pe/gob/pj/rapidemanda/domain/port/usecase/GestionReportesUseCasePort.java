package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.Date;
import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoPretensionItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandantePetitorioItem;

public interface GestionReportesUseCasePort {

    List<ConteoPetitorioTipoItem> obtenerConteosPetitorioPorTipo(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    List<ConteoPetitorioTipoPretensionItem> obtenerConteosPetitorioPorTipoYPrincipal(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    DemandanteConteos obtenerDemandanteConteosPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    List<DemandantePetitorioItem> obtenerDemandantesPetitorioPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;
}
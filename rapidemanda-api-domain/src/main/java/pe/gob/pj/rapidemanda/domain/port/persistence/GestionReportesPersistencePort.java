package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.Date;
import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoPretensionItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandantePetitorioItem;

public interface GestionReportesPersistencePort {

    List<ConteoPetitorioTipoItem> contarPetitoriosPorTipo(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    List<ConteoPetitorioTipoPretensionItem> contarPetitoriosPorTipoYPrincipal(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    DemandanteConteos contarDemandantesSexoEdadPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    List<DemandantePetitorioItem> listarDemandantesPetitorioPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;
}
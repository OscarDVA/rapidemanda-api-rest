package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.Date;
import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoPretensionItem;

public interface GestionReportesPersistencePort {

    List<ConteoPetitorioTipoItem> contarPetitoriosPorTipo(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;

    List<ConteoPetitorioTipoPretensionItem> contarPetitoriosPorTipoYPrincipal(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception;
}
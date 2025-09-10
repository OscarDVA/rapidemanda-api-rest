package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;

public interface GestionConceptoUseCasePort {
	 List<CatalogoConcepto> buscarConcepto(String cuo, Map<String, Object> filters) throws Exception;
}

package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;



public interface  GestionConceptoPersistencePort {
	 List<CatalogoConcepto> buscarConcepto(String cuo, Map<String, Object> filters) throws Exception;
}

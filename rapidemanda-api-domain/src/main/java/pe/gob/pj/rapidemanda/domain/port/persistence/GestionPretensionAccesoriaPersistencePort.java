package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;

public interface  GestionPretensionAccesoriaPersistencePort {
	 List<CatalogoPretensionAccesoria> buscarPretensionAccesoria(String cuo, Map<String, Object> filters) throws Exception;
}

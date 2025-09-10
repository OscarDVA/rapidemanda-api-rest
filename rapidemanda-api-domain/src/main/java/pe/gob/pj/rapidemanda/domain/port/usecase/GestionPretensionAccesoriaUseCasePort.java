package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;

public interface GestionPretensionAccesoriaUseCasePort {
	 List<CatalogoPretensionAccesoria> buscarPretensionAccesoria(String cuo, Map<String, Object> filters) throws Exception;
}

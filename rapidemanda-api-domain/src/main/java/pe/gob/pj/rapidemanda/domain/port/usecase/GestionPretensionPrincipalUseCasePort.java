package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;

public interface GestionPretensionPrincipalUseCasePort {
	 List<CatalogoPretensionPrincipal> buscarPretensionPrincipal(String cuo, Map<String, Object> filters) throws Exception;
}

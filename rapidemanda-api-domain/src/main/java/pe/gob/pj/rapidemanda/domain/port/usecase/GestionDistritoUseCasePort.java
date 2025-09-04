package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;

public interface GestionDistritoUseCasePort {

	public List<Distrito> buscarDistrito(String cuo, Map<String, Object> filters) throws Exception;

}

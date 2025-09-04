package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;

public interface GestionProvinciaPersistencePort {
	
	public List<Provincia> buscarProvincia(String cuo, Map<String, Object> filters) throws Exception;

	

}

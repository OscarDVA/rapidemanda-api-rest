package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;

public interface GestionDepartamentoUseCasePort {

	public List<Departamento> buscarDepartamento(String cuo, Map<String, Object> filters) throws Exception;

}

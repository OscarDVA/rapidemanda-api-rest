package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDepartamentoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDepartamentoUseCasePort;

@Service("gestionDepartamentoUseCasePort")
public class GestionDepartamentoUseCaseAdapter implements GestionDepartamentoUseCasePort {
	final GestionDepartamentoPersistencePort gestionDepartamentoPersistencePort;

	public GestionDepartamentoUseCaseAdapter(
			@Qualifier("gestionDepartamentoPersistencePort") GestionDepartamentoPersistencePort gestionDepartamentoPersistencePort) {
		this.gestionDepartamentoPersistencePort = gestionDepartamentoPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Departamento> buscarDepartamento(String cuo, Map<String, Object> filters) throws Exception {
		List<Departamento> lista = gestionDepartamentoPersistencePort.buscarDepartamento(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEPARTAMENTO_CONSULTAR.getNombre()));
		}
		return lista;
	}
}

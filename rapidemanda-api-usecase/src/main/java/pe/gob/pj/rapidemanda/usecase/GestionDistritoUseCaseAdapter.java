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
import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDistritoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDistritoUseCasePort;

@Service("gestionDistritoUseCasePort")
public class GestionDistritoUseCaseAdapter implements GestionDistritoUseCasePort {
	final GestionDistritoPersistencePort gestionDistritoPersistencePort;

	public GestionDistritoUseCaseAdapter(
			@Qualifier("gestionDistritoPersistencePort") GestionDistritoPersistencePort gestionDistritoPersistencePort) {
		this.gestionDistritoPersistencePort = gestionDistritoPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Distrito> buscarDistrito(String cuo, Map<String, Object> filters) throws Exception {
		List<Distrito> lista = gestionDistritoPersistencePort.buscarDistrito(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DISTRITO_CONSULTAR.getNombre()));
		}
		return lista;
	}
}

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
import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionProvinciaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionProvinciaUseCasePort;

@Service("gestionProvinciaUseCasePort")
public class GestionProvinciaUseCaseAdapter implements GestionProvinciaUseCasePort {
	final GestionProvinciaPersistencePort gestionProvinciaPersistencePort;

	public GestionProvinciaUseCaseAdapter(
			@Qualifier("gestionProvinciaPersistencePort") GestionProvinciaPersistencePort gestionProvinciaPersistencePort) {
		this.gestionProvinciaPersistencePort = gestionProvinciaPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Provincia> buscarProvincia(String cuo, Map<String, Object> filters) throws Exception {
		List<Provincia> lista = gestionProvinciaPersistencePort.buscarProvincia(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.PROVINCIA_CONSULTAR.getNombre()));
		}
		return lista;
	}
}

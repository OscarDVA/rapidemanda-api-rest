package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPretensionAccesoriaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionAccesoriaUseCasePort;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service("gestionPretensionAccesoriaUseCasePort")
public class GestionCatalogoPretensionAccesoriaUseCaseAdapter implements GestionPretensionAccesoriaUseCasePort {
	
	private final GestionPretensionAccesoriaPersistencePort gestionPretensionAccesoriaPersistencePort;

	public GestionCatalogoPretensionAccesoriaUseCaseAdapter(
			@Qualifier("gestionPretensionAccesoriaPersistencePort") GestionPretensionAccesoriaPersistencePort gestionPretensionAccesoriaPersistencePort) {
		this.gestionPretensionAccesoriaPersistencePort = gestionPretensionAccesoriaPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<CatalogoPretensionAccesoria> buscarPretensionAccesoria(String cuo, Map<String, Object> filters)
			throws Exception {

		List<CatalogoPretensionAccesoria> lista = gestionPretensionAccesoriaPersistencePort
				.buscarPretensionAccesoria(cuo, filters);

		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(), String
					.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.CATALOGO_PRETENSION_ACCESORIA_CONSULTAR.getNombre()));
		}

		return lista;
	}
}
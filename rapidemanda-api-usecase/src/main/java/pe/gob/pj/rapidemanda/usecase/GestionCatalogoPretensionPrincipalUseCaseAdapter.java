package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPretensionPrincipalPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionPrincipalUseCasePort;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service("gestionPretensionPrincipalUseCasePort")

public class GestionCatalogoPretensionPrincipalUseCaseAdapter implements GestionPretensionPrincipalUseCasePort {
	
	private final GestionPretensionPrincipalPersistencePort gestionPretensionPrincipalPersistencePort;

	public GestionCatalogoPretensionPrincipalUseCaseAdapter(
			@Qualifier("gestionPretensionPrincipalPersistencePort") GestionPretensionPrincipalPersistencePort gestionPretensionPrincipalPersistencePort) {
		this.gestionPretensionPrincipalPersistencePort = gestionPretensionPrincipalPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })

	public List<CatalogoPretensionPrincipal> buscarPretensionPrincipal(String cuo, Map<String, Object> filters)
			throws Exception {

		List<CatalogoPretensionPrincipal> lista = gestionPretensionPrincipalPersistencePort
				.buscarPretensionPrincipal(cuo, filters);

		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(), String
					.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.CATALOGO_PETITORIO_CONSULTAR.getNombre()));
		}

		return lista;
	}
}

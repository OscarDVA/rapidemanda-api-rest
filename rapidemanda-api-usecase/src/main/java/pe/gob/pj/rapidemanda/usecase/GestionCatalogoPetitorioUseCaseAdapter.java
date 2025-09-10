package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPetitorioPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPetitorioUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionPetitorioUseCasePort")

public class GestionCatalogoPetitorioUseCaseAdapter implements GestionPetitorioUseCasePort {
	private final GestionPetitorioPersistencePort gestionPetitorioPersistencePort;

	public GestionCatalogoPetitorioUseCaseAdapter(
			@Qualifier("gestionPetitorioPersistencePort") GestionPetitorioPersistencePort gestionPetitorioPersistencePort) {
		this.gestionPetitorioPersistencePort = gestionPetitorioPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })

	public List<CatalogoPetitorio> buscarPetitorio(String cuo) throws Exception {

		List<CatalogoPetitorio> petitorios = gestionPetitorioPersistencePort.buscarPetitorio(cuo);

		if (petitorios.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.CATALOGO_PETITORIO_CONSULTAR.getNombre()));
		}

		return petitorios;
	}
}

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
import pe.gob.pj.rapidemanda.domain.model.servicio.Perfil;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPerfilPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPerfilUseCasePort;

@Service("gestionPerfilUseCasePort")
public class GestionPerfilUseCaseAdapter implements GestionPerfilUseCasePort {

	private final GestionPerfilPersistencePort gestionPerfilPersistencePort;

	public GestionPerfilUseCaseAdapter(
			@Qualifier("gestionPerfilPersistencePort") GestionPerfilPersistencePort gestionPerfilPersistencePort) {
		this.gestionPerfilPersistencePort = gestionPerfilPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Perfil> buscarPerfiles(String cuo) throws Exception {

		List<Perfil> lista = gestionPerfilPersistencePort.buscarPerfiles(cuo);

		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.PERFIL_CONSULTAR.getNombre()));
		}

		return lista;
	}

}
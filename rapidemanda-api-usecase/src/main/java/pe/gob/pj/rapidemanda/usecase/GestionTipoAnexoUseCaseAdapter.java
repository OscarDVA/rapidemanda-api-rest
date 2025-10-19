package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoAnexo;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoAnexoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionTipoAnexoUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionTipoAnexoUseCasePort")
public class GestionTipoAnexoUseCaseAdapter implements GestionTipoAnexoUseCasePort {

	private final GestionTipoAnexoPersistencePort gestionTipoAnexoPersistencePort;

	public GestionTipoAnexoUseCaseAdapter(
			@Qualifier("gestionTipoAnexoPersistencePort") GestionTipoAnexoPersistencePort gestionTipoAnexoPersistencePort) {
		this.gestionTipoAnexoPersistencePort = gestionTipoAnexoPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<TipoAnexo> buscarTipoAnexos(String cuo) throws Exception {
		List<TipoAnexo> lista = gestionTipoAnexoPersistencePort.buscarTipoAnexos(cuo);

		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.TIPO_ANEXO_CONSULTAR.getNombre()));
		}

		return lista;
	}
}
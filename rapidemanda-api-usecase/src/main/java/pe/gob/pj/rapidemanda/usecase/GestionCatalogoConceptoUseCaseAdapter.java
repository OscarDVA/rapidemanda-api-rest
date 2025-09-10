package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionConceptoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionConceptoUseCasePort;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service("gestionConceptoUseCasePort")

public class GestionCatalogoConceptoUseCaseAdapter implements GestionConceptoUseCasePort {
	
	private final GestionConceptoPersistencePort gestionConceptoPersistencePort;

	public GestionCatalogoConceptoUseCaseAdapter(
			@Qualifier("gestionConceptoPersistencePort") GestionConceptoPersistencePort gestionConceptoPersistencePort) {
		this.gestionConceptoPersistencePort = gestionConceptoPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })

	public List<CatalogoConcepto> buscarConcepto(String cuo, Map<String, Object> filters)
			throws Exception {

		List<CatalogoConcepto> lista = gestionConceptoPersistencePort
				.buscarConcepto(cuo, filters);

		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(), String
					.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.CATALOGO_CONCEPTO_CONSULTAR.getNombre()));
		}

		return lista;
	}
}

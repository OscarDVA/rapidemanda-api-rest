package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionDemandaUseCasePort;

@Service("gestionDemandaUseCasePort")
public class GestionDemandaUseCaseAdapter implements GestionDemandaUseCasePort {

	final GestionDemandaPersistencePort gestionDemandaPersistencePort;

	public GestionDemandaUseCaseAdapter(
			@Qualifier("gestionDemandaPersistencePort") GestionDemandaPersistencePort gestionDemandaPersistencePort) {
		this.gestionDemandaPersistencePort = gestionDemandaPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })

	public List<Demanda> buscarDemandas(String cuo, Map<String, Object> filters) throws Exception {
		List<Demanda> lista = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_CONSULTAR.getNombre()));
		}
		return lista;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void registrarDemanda(String cuo, Demanda demanda) throws Exception {

		gestionDemandaPersistencePort.registrarDemanda(cuo, demanda);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })

	public void actualizarDemanda(String cuo, Demanda demanda) throws Exception {
		Map<String, Object> filters = new HashMap<>();
		filters.put(Demanda.P_ID, demanda.getId());

		List<Demanda> demandas = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);

		if (demandas.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()));
		}

		Demanda demandaExistente = demandas.get(0);

		if (!"B".equals(demandaExistente.getIdEstadoDemanda())) {
			throw new ErrorException(Errors.NEGOCIO_DEMANDA_NO_EDITABLE.getCodigo(),
					String.format(Errors.NEGOCIO_DEMANDA_NO_EDITABLE.getNombre(), demandaExistente.getEstadoDemanda()));
		}

		// Asegurar que no se cambie el estado mediante actualización
		demanda.setIdEstadoDemanda(demandaExistente.getIdEstadoDemanda());

		gestionDemandaPersistencePort.actualizarDemanda(cuo, demanda);
	}

}

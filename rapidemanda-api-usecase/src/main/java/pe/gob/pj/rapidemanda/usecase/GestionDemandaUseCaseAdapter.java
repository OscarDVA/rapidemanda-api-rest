package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Date;
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
import pe.gob.pj.rapidemanda.usecase.service.DemandaCompletenessService;
import pe.gob.pj.rapidemanda.usecase.service.DemandaCompletenessService.DemandaCompletenessResult;

@Service("gestionDemandaUseCasePort")
public class GestionDemandaUseCaseAdapter implements GestionDemandaUseCasePort {

    final GestionDemandaPersistencePort gestionDemandaPersistencePort;
    final DemandaCompletenessService demandaCompletenessService;

    public GestionDemandaUseCaseAdapter(
            @Qualifier("gestionDemandaPersistencePort") GestionDemandaPersistencePort gestionDemandaPersistencePort,
            @Qualifier("demandaCompletenessService") DemandaCompletenessService demandaCompletenessService) {
        this.gestionDemandaPersistencePort = gestionDemandaPersistencePort;
        this.demandaCompletenessService = demandaCompletenessService;
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

		// Registro principal de la demanda
		gestionDemandaPersistencePort.registrarDemanda(cuo, demanda);

		// Si el registro asignó ID, revalidar completitud y evaluar transición
		if (demanda.getId() != null) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(Demanda.P_ID, demanda.getId());

			List<Demanda> registradas = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);
			if (!registradas.isEmpty()) {
				Demanda demandaRegistrada = registradas.get(0);

				DemandaCompletenessResult resultado = demandaCompletenessService.validar(demandaRegistrada);
				boolean puedeTransicionar = resultado.isCompleta() && "B".equals(demandaRegistrada.getIdEstadoDemanda());

                if (puedeTransicionar) {
                    demandaRegistrada.setIdEstadoDemanda("C");
                    gestionDemandaPersistencePort.actualizarDemanda(cuo, demandaRegistrada);
                }
			}
		}
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

        // Asegurar que no se cambie el estado mediante actualización externa
        demanda.setIdEstadoDemanda(demandaExistente.getIdEstadoDemanda());

        // Actualización principal
        gestionDemandaPersistencePort.actualizarDemanda(cuo, demanda);

        // Releer demanda actualizada para evaluar completitud
        List<Demanda> postUpdate = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);
        if (!postUpdate.isEmpty()) {
            Demanda demandaActualizada = postUpdate.get(0);

            DemandaCompletenessResult resultado = demandaCompletenessService.validar(demandaActualizada);
            boolean puedeTransicionar = resultado.isCompleta() && "B".equals(demandaActualizada.getIdEstadoDemanda());

            if (puedeTransicionar) {
                demandaActualizada.setIdEstadoDemanda("C");
                gestionDemandaPersistencePort.actualizarDemanda(cuo, demandaActualizada);
            }
        }
    }

    @Override
    @Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
            Exception.class, SQLException.class })
    public void actualizarCamposDemanda(String cuo, Integer idDemanda, String nuevoEstadoDemanda, String TipoRecepcion,
                                        Date fechaRecepcion, Integer idUsuarioRecepcion) throws Exception {

        if (nuevoEstadoDemanda == null || !"P".equals(nuevoEstadoDemanda)) {
            throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(),
                    String.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.ESTADO_ACTUALIZAR.getNombre()));
        }

        Map<String, Object> filters = new HashMap<>();
        filters.put(Demanda.P_ID, idDemanda);

        List<Demanda> demandas = gestionDemandaPersistencePort.buscarDemandas(cuo, filters);
        if (demandas.isEmpty()) {
            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_ACTUALIZAR.getNombre()));
        }

        Demanda demandaActual = demandas.get(0);
        if (!"C".equals(demandaActual.getIdEstadoDemanda())) {
            throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(),
                    String.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.ESTADO_ACTUALIZAR.getNombre()));
        }

        // Ignorar cualquier fecha recibida y usar la fecha actual del servidor
        Date fechaServidor = new Date();
        gestionDemandaPersistencePort.actualizarCamposDemanda(cuo, idDemanda, nuevoEstadoDemanda, TipoRecepcion,
                fechaServidor, idUsuarioRecepcion);
    }

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, rollbackFor = {
			Exception.class, SQLException.class })
	public void eliminar(String cuo, Integer id) throws Exception {
		gestionDemandaPersistencePort.eliminar(cuo, id);
	}

}

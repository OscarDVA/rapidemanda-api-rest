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
import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPersonaUseCasePort;

@Service("gestionPersonaUseCasePort")
public class GestionPersonaUseCaseAdapter implements GestionPersonaUseCasePort {

	final GestionPersonaPersistencePort gestionPersonaPersistencePort;

	public GestionPersonaUseCaseAdapter(
			@Qualifier("gestionPersonaPersistencePort") GestionPersonaPersistencePort gestionPersonaPersistencePort) {
		this.gestionPersonaPersistencePort = gestionPersonaPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Persona> buscarPersona(String cuo, Map<String, Object> filters) throws Exception {
		List<Persona> lista = gestionPersonaPersistencePort.buscarPersona(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.PERSONA_CONSULTAR.getNombre()));
		}
		return lista;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void registrarPersona(String cuo, Persona persona) throws Exception {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(Persona.P_NUMERO_DOCUMENTO, persona.getNumeroDocumento());
		if (!gestionPersonaPersistencePort.buscarPersona(cuo, filters).isEmpty()) {
			throw new ErrorException(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getCodigo(), String
					.format(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getNombre(), Proceso.PERSONA_REGISTRAR.getNombre()));
		}
		gestionPersonaPersistencePort.registrarPersona(cuo, persona);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void actualizarPersona(String cuo, Persona persona) throws Exception {
		// Validar que no exista otra persona con el mismo número de documento (excepto él mismo para casos de actualización)
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(Persona.P_NUMERO_DOCUMENTO, persona.getNumeroDocumento());

		List<Persona> personasMismoNombre = gestionPersonaPersistencePort.buscarPersona(cuo, filters);

		if (!personasMismoNombre.isEmpty() && !personasMismoNombre.get(0).getId().equals(persona.getId())) {
			throw new ErrorException(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getCodigo(), String
					.format(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getNombre(), Proceso.PERSONA_REGISTRAR.getNombre()));
		}
		// Continuar con la actualización
		gestionPersonaPersistencePort.actualizarPersona(cuo, persona);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void cambiarEstadoPersona(String cuo, Integer idPersona, String nuevoEstado) throws Exception {
		// Validaciones de negocio específicas
		if (!"0".equals(nuevoEstado) && !"1".equals(nuevoEstado)) {
			throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(),
					String.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.PERSONA_ACTUALIZAR.getNombre()));
		}
		gestionPersonaPersistencePort.actualizarEstadoPersona(cuo, idPersona, nuevoEstado);
	}

}

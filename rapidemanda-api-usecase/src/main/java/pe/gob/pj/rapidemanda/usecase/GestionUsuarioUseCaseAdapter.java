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
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionUsuarioPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionUsuarioUseCasePort;

@Service("gestionUsuarioUseCasePort")
public class GestionUsuarioUseCaseAdapter implements GestionUsuarioUseCasePort {

	private final GestionUsuarioPersistencePort gestionUsuarioPersistencePort;
	private final GestionPersonaPersistencePort gestionPersonaPersistencePort;

	public GestionUsuarioUseCaseAdapter(
			@Qualifier("gestionUsuarioPersistencePort") GestionUsuarioPersistencePort gestionUsuarioPersistencePort,
			@Qualifier("gestionPersonaPersistencePort") GestionPersonaPersistencePort gestionPersonaPersistencePort) {
		this.gestionUsuarioPersistencePort = gestionUsuarioPersistencePort;
		this.gestionPersonaPersistencePort = gestionPersonaPersistencePort;
	}
	
	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception {
		List<Usuario> lista = gestionUsuarioPersistencePort.buscarUsuario(cuo, filters);
		if (lista.isEmpty()) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.USUARIO_CONSULTAR.getNombre()));
		}
		return lista;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class })
	public void registrarUsuario(String cuo, Usuario usuario) throws Exception {
		try {
			// Validar que la persona no exista
			String numeroDocumento = usuario.getPersona().getNumeroDocumento();
			Map<String, Object> filters = new HashMap<>();
			filters.put(Persona.P_NUMERO_DOCUMENTO, numeroDocumento);

			List<Persona> personas = gestionPersonaPersistencePort.buscarPersona(cuo, filters);
			if (!personas.isEmpty()) {
				throw new ErrorException(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getCodigo(),
						String.format(Errors.NEGOCIO_PERSONA_YA_REGISTRADA.getNombre(), numeroDocumento));
			}
			// Validar que el usuario no exista
			 String nombreUsuario = usuario.getUsuario();
			 Map<String, Object> filtersUsuario = new HashMap<>();
			 filtersUsuario.put(Usuario.P_NOMBRE_USUARIO, nombreUsuario);
			 List<Usuario> usuarios = gestionUsuarioPersistencePort.buscarUsuario(cuo, filtersUsuario);
			 if (!usuarios.isEmpty()) {
				 throw new ErrorException(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getCodigo(),
						String.format(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getNombre(), nombreUsuario));
			 }

			// Registrar usuario (incluye persona y perfil)
			gestionUsuarioPersistencePort.registrarUsuario(cuo, usuario);
		} catch (ErrorException ee) {
			// Re-lanzar directamente los errores de negocio
			throw ee;
		} catch (Exception e) {
			throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.USUARIO_REGISTRAR.getNombre()),
					e.getMessage(), e.getCause());
		}
	}
}

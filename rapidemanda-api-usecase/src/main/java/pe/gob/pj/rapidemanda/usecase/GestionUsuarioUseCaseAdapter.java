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
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionUsuarioPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionUsuarioUseCasePort;

@Service("gestionUsuarioUseCasePort")
public class GestionUsuarioUseCaseAdapter implements GestionUsuarioUseCasePort {

	private final GestionUsuarioPersistencePort gestionUsuarioPersistencePort;

	public GestionUsuarioUseCaseAdapter(
			@Qualifier("gestionUsuarioPersistencePort") GestionUsuarioPersistencePort gestionUsuarioPersistencePort) {
		this.gestionUsuarioPersistencePort = gestionUsuarioPersistencePort;
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
	public void crearUsuario(String cuo, Usuario usuario) throws Exception {
		Map<String, Object> filtersUsuario = new HashMap<String, Object>();
		filtersUsuario.put(Usuario.P_NOMBRE_USUARIO, usuario.getUsuario());

		if (!gestionUsuarioPersistencePort.buscarUsuario(cuo, filtersUsuario).isEmpty()) {
			throw new ErrorException(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getCodigo(), String
					.format(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getNombre(), Proceso.USUARIO_REGISTRAR.getNombre()));
		}
		gestionUsuarioPersistencePort.crearUsuario(cuo, usuario);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void actualizarUsuario(String cuo, Usuario usuario) throws Exception {

		Map<String, Object> filtersUsuario = new HashMap<>();
		filtersUsuario.put(Usuario.P_NOMBRE_USUARIO, usuario.getUsuario());

		List<Usuario> usuariosMismoNombre = gestionUsuarioPersistencePort.buscarUsuario(cuo, filtersUsuario);

		if (!usuariosMismoNombre.isEmpty()
				&& !usuariosMismoNombre.get(0).getIdUsuario().equals(usuario.getIdUsuario())) {
			throw new ErrorException(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getCodigo(), String
					.format(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getNombre(), Proceso.USUARIO_ACTUALIZAR.getNombre()));
		}

		gestionUsuarioPersistencePort.actualizarUsuario(cuo, usuario);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void actualizarEstadoUsuario(String cuo, Integer id, String nuevoEstado) throws Exception {
		if (!"0".equals(nuevoEstado) && !"1".equals(nuevoEstado)) {
			throw new ErrorException(Errors.NEGOCIO_ESTADO_INVALIDO.getCodigo(),
					String.format(Errors.NEGOCIO_ESTADO_INVALIDO.getNombre(), Proceso.PERSONA_ACTUALIZAR.getNombre()));
		}
		gestionUsuarioPersistencePort.actualizarEstadoUsuario(cuo, id, nuevoEstado);
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public void resetearClaveUsuario(String cuo, Integer id, String nuevaClave) throws Exception {
		if (nuevaClave == null || nuevaClave.trim().isEmpty()) {
			throw new ErrorException(Errors.NEGOCIO_PARAMETRO_REQUERIDO.getCodigo(),
					String.format(Errors.NEGOCIO_PARAMETRO_REQUERIDO.getNombre(), "nuevaClave"));
		}
		gestionUsuarioPersistencePort.resetearClaveUsuario(cuo, id, nuevaClave);
	}
}

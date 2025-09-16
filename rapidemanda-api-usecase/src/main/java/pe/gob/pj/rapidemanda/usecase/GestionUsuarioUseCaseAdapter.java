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
			 throw new ErrorException(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getCodigo(),
					String.format(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getNombre(), Proceso.USUARIO_REGISTRAR.getNombre()));
		 }
		 gestionUsuarioPersistencePort.crearUsuario(cuo, usuario);
	}
}

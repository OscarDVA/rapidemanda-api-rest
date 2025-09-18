package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;

public interface GestionUsuarioUseCasePort {

	public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception;

	public void crearUsuario(String cuo, Usuario usuario) throws Exception;

	public void actualizarUsuario(String cuo, Usuario usuario) throws Exception;
	
	public void actualizarEstadoUsuario(String cuo, Integer id, String nuevoEstado) throws Exception;
	
	public void resetearClaveUsuario(String cuo, Integer id, String nuevaClave) throws Exception;
}

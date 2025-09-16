package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;

public interface GestionUsuarioPersistencePort {

	public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception;

	public void crearUsuario(String cuo, Usuario usuario) throws Exception;

	public void actualizarUsuario(String cuo, Usuario usuario) throws Exception;
}

package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;

public interface GestionUsuarioUseCasePort {
	void registrarUsuario(String cuo, Usuario usuario) throws Exception;
	 public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception;
}

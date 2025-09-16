package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.Perfil;

public interface PerfilPersistencePort {

	public List<Perfil> buscarPerfil(String cuo) throws Exception;

}

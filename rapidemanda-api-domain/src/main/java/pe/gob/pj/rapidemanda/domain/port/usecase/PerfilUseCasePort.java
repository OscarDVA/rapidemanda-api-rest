package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.Perfil;

public interface PerfilUseCasePort {

	public List<Perfil> buscarPerfil(String cuo) throws Exception;

}

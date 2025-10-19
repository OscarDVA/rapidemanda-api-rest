package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoAnexo;

public interface GestionTipoAnexoUseCasePort {

	public List<TipoAnexo> buscarTipoAnexos(String cuo) throws Exception;

}
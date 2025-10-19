package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoAnexo;

public interface GestionTipoAnexoPersistencePort {	
	public List<TipoAnexo> buscarTipoAnexos(String cuo) throws Exception;
}
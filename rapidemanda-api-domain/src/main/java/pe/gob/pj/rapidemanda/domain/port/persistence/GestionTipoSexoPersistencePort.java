package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoSexo;

public interface GestionTipoSexoPersistencePort {
    List<TipoSexo> buscarTiposSexo(String cuo) throws Exception;
}
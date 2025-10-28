package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoSexo;

public interface GestionTipoSexoUseCasePort {
    List<TipoSexo> buscarTiposSexo(String cuo) throws Exception;
}
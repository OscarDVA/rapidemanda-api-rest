package pe.gob.pj.rapidemanda.domain.port.persistence;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoDocumentoPersona;

import java.util.List;
import java.util.Map;

public interface GestionTipoDocumentoPersonaPersistencePort {
    List<TipoDocumentoPersona> buscarTiposDocumentoPersona(String cuo, Map<String, Object> filters) throws Exception;
}
package pe.gob.pj.rapidemanda.domain.port.usecase;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoDocumentoPersona;

import java.util.List;
import java.util.Map;

public interface GestionTipoDocumentoPersonaUseCasePort {
    List<TipoDocumentoPersona> buscarTiposDocumentoPersona(String cuo, Map<String, Object> filters) throws Exception;
}
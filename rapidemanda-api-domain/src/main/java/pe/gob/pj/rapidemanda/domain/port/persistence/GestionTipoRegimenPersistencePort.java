package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoRegimen;

public interface GestionTipoRegimenPersistencePort {
    List<TipoRegimen> buscarTiposRegimen(String cuo) throws Exception;
}
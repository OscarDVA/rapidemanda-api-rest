package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoRegimen;

public interface GestionTipoRegimenUseCasePort {
    List<TipoRegimen> buscarTiposRegimen(String cuo) throws Exception;
}
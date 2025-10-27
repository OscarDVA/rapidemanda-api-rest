package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoPresentacionDemanda;

public interface GestionTipoPresentacionDemandaPersistencePort {
    List<TipoPresentacionDemanda> buscarTiposPresentacionDemanda(String cuo) throws Exception;
}
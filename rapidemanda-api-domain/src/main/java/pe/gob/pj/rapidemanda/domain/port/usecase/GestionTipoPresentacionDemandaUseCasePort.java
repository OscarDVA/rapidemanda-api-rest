package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoPresentacionDemanda;

public interface GestionTipoPresentacionDemandaUseCasePort {
    List<TipoPresentacionDemanda> buscarTiposPresentacionDemanda(String cuo) throws Exception;
}
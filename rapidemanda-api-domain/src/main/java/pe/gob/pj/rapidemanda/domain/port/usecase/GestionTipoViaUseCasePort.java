package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoVia;

public interface GestionTipoViaUseCasePort {
    List<TipoVia> buscarTiposVia(String cuo) throws Exception;
}
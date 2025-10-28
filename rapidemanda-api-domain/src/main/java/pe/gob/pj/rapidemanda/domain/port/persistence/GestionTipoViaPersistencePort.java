package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoVia;

public interface GestionTipoViaPersistencePort {
    List<TipoVia> buscarTiposVia(String cuo) throws Exception;
}
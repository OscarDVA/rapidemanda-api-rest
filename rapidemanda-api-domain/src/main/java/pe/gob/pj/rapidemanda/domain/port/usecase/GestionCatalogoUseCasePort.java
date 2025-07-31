package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;

public interface GestionCatalogoUseCasePort {
	 List<CatalogoPetitorio> buscarCatalogo(String cuo) throws Exception;
}

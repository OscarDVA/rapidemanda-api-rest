package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;

public interface GestionPetitorioUseCasePort {
	 List<CatalogoPetitorio> buscarPetitorio(String cuo) throws Exception;
}

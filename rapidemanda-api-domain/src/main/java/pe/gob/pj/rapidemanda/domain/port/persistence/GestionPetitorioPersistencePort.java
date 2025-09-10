package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;



public interface  GestionPetitorioPersistencePort {
	 List<CatalogoPetitorio> buscarPetitorio(String cuo) throws Exception;
}

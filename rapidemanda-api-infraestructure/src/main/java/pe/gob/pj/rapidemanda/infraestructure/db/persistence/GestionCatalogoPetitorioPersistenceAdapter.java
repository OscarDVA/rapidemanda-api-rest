package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPetitorioPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoPetitorio;

import java.util.ArrayList;
import java.util.List;

@Component("gestionPetitorioPersistencePort")
public class GestionCatalogoPetitorioPersistenceAdapter implements GestionPetitorioPersistencePort {
	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<CatalogoPetitorio> buscarPetitorio(String cuo) throws Exception {
		List<CatalogoPetitorio> lista = new ArrayList<>();

		TypedQuery<MaeCatalogoPetitorio> query = this.sf.getCurrentSession()
				.createNamedQuery(MaeCatalogoPetitorio.Q_ALL, MaeCatalogoPetitorio.class);
		query.getResultStream().forEach(maePetitorio -> {
			CatalogoPetitorio pDto = new CatalogoPetitorio();
			pDto.setId(maePetitorio.getId());
			pDto.setNombre(maePetitorio.getNombre());
			pDto.setActivo(maePetitorio.getActivo());
			lista.add(pDto);
		});

		return lista;
	}

}

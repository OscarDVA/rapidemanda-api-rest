package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPretensionPrincipalPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoPretension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("gestionPretensionPrincipalPersistencePort")
public class GestionCatalogoPretensionPrincipalPersistenceAdapter implements GestionPretensionPrincipalPersistencePort {
	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<CatalogoPretensionPrincipal> buscarPretensionPrincipal(String cuo, Map<String, Object> filters)
			throws Exception {
		List<CatalogoPretensionPrincipal> lista = new ArrayList<>();
		
		if (!ProjectUtils.isNullOrEmpty(filters.get(CatalogoPretensionPrincipal.P_PETITORIO_ID))) {
            this.sf.getCurrentSession().enableFilter(MaeCatalogoPretension.F_PETITORIO_FILTER).setParameter(
                    MaeCatalogoPretension.P_PETITORIO_ID, filters.get(CatalogoPretensionPrincipal.P_PETITORIO_ID));
        }

		TypedQuery<MaeCatalogoPretension> query = this.sf.getCurrentSession()
				.createNamedQuery(MaeCatalogoPretension.Q_ALL, MaeCatalogoPretension.class);
		query.getResultStream().forEach(maeCatalogoPretension -> {
			CatalogoPretensionPrincipal ppDto = new CatalogoPretensionPrincipal();
			ppDto.setId(maeCatalogoPretension.getId());
			ppDto.setNombre(maeCatalogoPretension.getNombre());
			ppDto.setPetitorioId(maeCatalogoPretension.getPetitorio().getId());
			ppDto.setTieneConceptos(maeCatalogoPretension.getTieneConceptos());
			ppDto.setActivo(maeCatalogoPretension.getActivo());
			lista.add(ppDto);
		});

		return lista;
	}

}

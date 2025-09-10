package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPretensionAccesoriaPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoAccesorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("gestionPretensionAccesoriaPersistencePort")
public class GestionCatalogoPretensionAccesoriaPersistenceAdapter implements GestionPretensionAccesoriaPersistencePort {
	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<CatalogoPretensionAccesoria> buscarPretensionAccesoria(String cuo, Map<String, Object> filters)
			throws Exception {

		List<CatalogoPretensionAccesoria> lista = new ArrayList<>();

		if (!ProjectUtils.isNullOrEmpty(filters.get(CatalogoPretensionAccesoria.P_CONCEPTO_ID))) {
            this.sf.getCurrentSession().enableFilter(MaeCatalogoAccesorio.F_CONCEPTO_FILTER).setParameter(
                    MaeCatalogoAccesorio.P_CONCEPTO_ID, filters.get(CatalogoPretensionAccesoria.P_CONCEPTO_ID));
        }

        if (!ProjectUtils.isNullOrEmpty(filters.get(CatalogoPretensionAccesoria.P_PRETENSION_PRINCIPAL_ID))) {
            this.sf.getCurrentSession().enableFilter(MaeCatalogoAccesorio.F_PRETENSION_PRINCIPAL_FILTER).setParameter(
                    MaeCatalogoAccesorio.P_PRETENSION_PRINCIPAL_ID,
                    filters.get(CatalogoPretensionAccesoria.P_PRETENSION_PRINCIPAL_ID));
        }

		TypedQuery<MaeCatalogoAccesorio> query = this.sf.getCurrentSession()
				.createNamedQuery(MaeCatalogoAccesorio.Q_ALL, MaeCatalogoAccesorio.class);
		query.getResultStream().forEach(maeCatalogoAccesorio -> {
			CatalogoPretensionAccesoria paDto = new CatalogoPretensionAccesoria();
			paDto.setId(maeCatalogoAccesorio.getId());
			paDto.setNombre(maeCatalogoAccesorio.getNombre());
			
			if (maeCatalogoAccesorio.getConcepto() != null) {
				paDto.setConceptoId(maeCatalogoAccesorio.getConcepto().getId());
			}
			
			if (maeCatalogoAccesorio.getPretension() != null) {
				paDto.setPretensionPrincipalId(maeCatalogoAccesorio.getPretension().getId());
			}
			
			paDto.setActivo(maeCatalogoAccesorio.getActivo());
			lista.add(paDto);
		});

		return lista;
	}

}

package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionConceptoPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoConcepto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("gestionConceptoPersistencePort")
public class GestionCatalogoConceptoPersistenceAdapter implements GestionConceptoPersistencePort {
	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<CatalogoConcepto> buscarConcepto(String cuo, Map<String, Object> filters) throws Exception {

		List<CatalogoConcepto> lista = new ArrayList<>();

		if (!ProjectUtils.isNullOrEmpty(filters.get(CatalogoConcepto.P_PRETENSION_PRINCIPAL_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeCatalogoConcepto.F_PRETENSION_PRINCIPAL_FILTER).setParameter(
					MaeCatalogoConcepto.P_PRETENSION_PRINCIPAL_ID,
					filters.get(CatalogoConcepto.P_PRETENSION_PRINCIPAL_ID));
		}

		TypedQuery<MaeCatalogoConcepto> query = this.sf.getCurrentSession().createNamedQuery(MaeCatalogoConcepto.Q_ALL,
				MaeCatalogoConcepto.class);
		query.getResultStream().forEach(maeCatalogoConcepto -> {
			CatalogoConcepto cDto = new CatalogoConcepto();
			cDto.setId(maeCatalogoConcepto.getId());
			cDto.setNombre(maeCatalogoConcepto.getNombre());
			cDto.setPretensionPrincipalId(maeCatalogoConcepto.getPretension().getId());
			cDto.setActivo(maeCatalogoConcepto.getActivo());
			lista.add(cDto);
		});

		return lista;
	}

}

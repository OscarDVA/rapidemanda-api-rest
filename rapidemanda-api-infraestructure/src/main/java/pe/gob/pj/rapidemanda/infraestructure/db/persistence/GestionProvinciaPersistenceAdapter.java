package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionProvinciaPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeProvincia;

@Component("gestionProvinciaPersistencePort")
public class GestionProvinciaPersistenceAdapter implements GestionProvinciaPersistencePort {

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<Provincia> buscarProvincia(String cuo, Map<String, Object> filters) throws Exception {
		List<Provincia> lista = new ArrayList<>();

		if (!ProjectUtils.isNullOrEmpty(filters.get(Provincia.P_DEPARTAMENTO_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeProvincia.F_DEPARTAMENTO_FILTER)
					.setParameter(MaeProvincia.P_DEPARTAMENTO_ID, filters.get(Provincia.P_DEPARTAMENTO_ID));
		}
		if (!ProjectUtils.isNullOrEmpty(filters.get(Provincia.P_PROVINCIA_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeProvincia.F_PROVINCIA_FILTER)
					.setParameter(MaeProvincia.P_PROVINCIA_ID, filters.get(Provincia.P_PROVINCIA_ID));
		}

		TypedQuery<MaeProvincia> query = this.sf.getCurrentSession().createNamedQuery(MaeProvincia.Q_ALL,
				MaeProvincia.class);
		query.getResultStream().forEach(maeProvincia -> {
			Provincia provinciaDto = new Provincia();
			provinciaDto.setId(maeProvincia.getId());
			provinciaDto.setNombre(maeProvincia.getNombre());
			provinciaDto.setDepartamentoId(maeProvincia.getDepartamento().getId());
			provinciaDto.setActivo(maeProvincia.getActivo());
			lista.add(provinciaDto);
		});
		return lista;
	}

}

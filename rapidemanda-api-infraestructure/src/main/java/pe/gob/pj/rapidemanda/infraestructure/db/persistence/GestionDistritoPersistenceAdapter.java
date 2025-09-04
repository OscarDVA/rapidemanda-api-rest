package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDistritoPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeDistrito;

@Component("gestionDistritoPersistencePort")
public class GestionDistritoPersistenceAdapter implements GestionDistritoPersistencePort{

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;
	
	@Override
	public List<Distrito> buscarDistrito(String cuo, Map<String, Object> filters) throws Exception {
		List<Distrito> lista = new ArrayList<>();
		if(!ProjectUtils.isNullOrEmpty(filters.get(Distrito.P_PROVINCIA_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeDistrito.F_PROVINCIA_FILTER)
				.setParameter(MaeDistrito.P_PROVINCIA_ID, filters.get(Distrito.P_PROVINCIA_ID));
		}
		
		if(!ProjectUtils.isNullOrEmpty(filters.get(Distrito.P_DISTRITO_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeDistrito.F_DISTRITO_FILTER)
				.setParameter(MaeDistrito.P_DISTRITO_ID, filters.get(Distrito.P_DISTRITO_ID));
		}
		
		TypedQuery<MaeDistrito> query = this.sf.getCurrentSession().createNamedQuery(MaeDistrito.Q_ALL, MaeDistrito.class);
		query.getResultStream().forEach(maeDistrito -> {
			Distrito distritoDto = new Distrito();
			distritoDto.setId(maeDistrito.getId());
			distritoDto.setNombre(maeDistrito.getNombre());
			distritoDto.setProvinciaId(maeDistrito.getProvincia().getId());
			distritoDto.setActivo(maeDistrito.getActivo());
			lista.add(distritoDto);
		});
		return lista;
	}

}

package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDepartamentoPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeDepartamento;

@Component("gestionDepartamentoPersistencePort")
public class GestionDepartamentoPersistenceAdapter implements GestionDepartamentoPersistencePort{

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;
	
	@Override
	public List<Departamento> buscarDepartamento(String cuo, Map<String, Object> filters) throws Exception {
		List<Departamento> lista = new ArrayList<>();
		if(!ProjectUtils.isNullOrEmpty(filters.get(Departamento.P_DEPARTAMENTO_ID))) {
			this.sf.getCurrentSession().enableFilter(MaeDepartamento.F_ID)
				.setParameter(MaeDepartamento.P_ID, filters.get(Departamento.P_DEPARTAMENTO_ID));
		}
		TypedQuery<MaeDepartamento> query = this.sf.getCurrentSession().createNamedQuery(MaeDepartamento.Q_ALL, MaeDepartamento.class);
		query.getResultStream().forEach(maeDepartamento -> {
			Departamento departamentoDto = new Departamento();
			departamentoDto.setId(maeDepartamento.getId());
			departamentoDto.setNombre(maeDepartamento.getNombre());
			departamentoDto.setActivo(maeDepartamento.getActivo());
			lista.add(departamentoDto);
		});
		return lista;
	}

}

package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoAnexo;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoAnexoPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoAnexo;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoAnexoEntityMapper;

@Repository("gestionTipoAnexoPersistencePort")
public class GestionTipoAnexoPersistenceAdapter implements GestionTipoAnexoPersistencePort {

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Autowired
	private TipoAnexoEntityMapper tipoAnexoEntityMapper;

	@Override
	public List<TipoAnexo> buscarTipoAnexos(String cuo) throws Exception {
		List<TipoAnexo> tiposAnexo = new ArrayList<>();

		Session session = this.sf.getCurrentSession();

		TypedQuery<MaeTipoAnexo> query = session.createNamedQuery(MaeTipoAnexo.Q_ALL, MaeTipoAnexo.class);
		query.getResultStream().map(tipoAnexoEntityMapper::toModel).forEach(tiposAnexo::add);

		return tiposAnexo;
	}
}
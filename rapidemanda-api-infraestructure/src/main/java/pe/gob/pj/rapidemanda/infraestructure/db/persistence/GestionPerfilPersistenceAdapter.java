package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.Perfil;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPerfilPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaePerfil;

@Repository("gestionPerfilPersistencePort")
public class GestionPerfilPersistenceAdapter implements GestionPerfilPersistencePort {
	
	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<Perfil> buscarPerfiles(String cuo) throws Exception {

		Session session = this.sf.getCurrentSession();
		// Ejecutar consulta
		TypedQuery<MaePerfil> query = session.createNamedQuery(MaePerfil.Q_ALL, MaePerfil.class);
		List<MaePerfil> entidades = query.getResultList();

		// Convertir entidades a modelo de dominio
		List<Perfil> perfiles = new ArrayList<>();
		for (MaePerfil entidad : entidades) {
			Perfil perfil = new Perfil();
			perfil.setId(entidad.getId());
			perfil.setNombre(entidad.getNombre());
			perfil.setRol(entidad.getRol());
			perfil.setActivo(entidad.getActivo());
			perfiles.add(perfil);
		}

		return perfiles;
	}

}
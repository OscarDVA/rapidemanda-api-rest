package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoRegimen;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoRegimenPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoRegimen;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoRegimenEntityMapper;

@Repository("gestionTipoRegimenPersistencePort")
public class GestionTipoRegimenPersistenceAdapter implements GestionTipoRegimenPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private TipoRegimenEntityMapper tipoRegimenEntityMapper;

    @Override
    public List<TipoRegimen> buscarTiposRegimen(String cuo) throws Exception {
        List<TipoRegimen> lista = new ArrayList<>();

        TypedQuery<MaeTipoRegimen> query = this.sf.getCurrentSession().createNamedQuery(MaeTipoRegimen.Q_ALL, MaeTipoRegimen.class);
        query.getResultStream().map(tipoRegimenEntityMapper::toModel).forEach(lista::add);

        return lista;
    }
}
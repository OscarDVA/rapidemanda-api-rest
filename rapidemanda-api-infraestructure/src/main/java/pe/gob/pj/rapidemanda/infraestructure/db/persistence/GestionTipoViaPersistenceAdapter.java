package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoVia;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoViaPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoVia;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoViaEntityMapper;

@Repository("gestionTipoViaPersistencePort")
public class GestionTipoViaPersistenceAdapter implements GestionTipoViaPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private TipoViaEntityMapper tipoViaEntityMapper;

    @Override
    public List<TipoVia> buscarTiposVia(String cuo) throws Exception {
        List<TipoVia> lista = new ArrayList<>();

        TypedQuery<MaeTipoVia> query = this.sf.getCurrentSession().createNamedQuery(MaeTipoVia.Q_ALL, MaeTipoVia.class);
        query.getResultStream().map(tipoViaEntityMapper::toModel).forEach(lista::add);

        return lista;
    }
}
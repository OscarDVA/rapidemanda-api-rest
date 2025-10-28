package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoSexo;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoSexoPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoSexo;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoSexoEntityMapper;

@Repository("gestionTipoSexoPersistencePort")
public class GestionTipoSexoPersistenceAdapter implements GestionTipoSexoPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private TipoSexoEntityMapper tipoSexoEntityMapper;

    @Override
    public List<TipoSexo> buscarTiposSexo(String cuo) throws Exception {
        List<TipoSexo> lista = new ArrayList<>();

        TypedQuery<MaeTipoSexo> query = this.sf.getCurrentSession().createNamedQuery(MaeTipoSexo.Q_ALL, MaeTipoSexo.class);
        query.getResultStream().map(tipoSexoEntityMapper::toModel).forEach(lista::add);

        return lista;
    }
}
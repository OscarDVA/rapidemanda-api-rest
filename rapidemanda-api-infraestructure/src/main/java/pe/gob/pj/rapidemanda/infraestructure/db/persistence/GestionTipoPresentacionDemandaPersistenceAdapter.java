package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoPresentacionDemanda;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoPresentacionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoPresentacion;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoPresentacionDemandaEntityMapper;

@Repository("gestionTipoPresentacionDemandaPersistencePort")
public class GestionTipoPresentacionDemandaPersistenceAdapter implements GestionTipoPresentacionDemandaPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private TipoPresentacionDemandaEntityMapper tipoPresentacionDemandaEntityMapper;

    @Override
    public List<TipoPresentacionDemanda> buscarTiposPresentacionDemanda(String cuo) throws Exception {
        List<TipoPresentacionDemanda> tipos = new ArrayList<>();

        Session session = this.sf.getCurrentSession();
        TypedQuery<MaeTipoPresentacion> query = session.createNamedQuery(MaeTipoPresentacion.Q_ALL, MaeTipoPresentacion.class);
        query.getResultStream().map(tipoPresentacionDemandaEntityMapper::toModel).forEach(tipos::add);

        return tipos;
    }
}
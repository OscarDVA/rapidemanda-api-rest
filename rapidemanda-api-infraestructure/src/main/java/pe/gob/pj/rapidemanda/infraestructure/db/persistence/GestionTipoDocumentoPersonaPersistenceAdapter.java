package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.TipoDocumentoPersona;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionTipoDocumentoPersonaPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoDocumentoPersona;
import pe.gob.pj.rapidemanda.infraestructure.mapper.TipoDocumentoPersonaEntityMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("gestionTipoDocumentoPersonaPersistencePort")
public class GestionTipoDocumentoPersonaPersistenceAdapter implements GestionTipoDocumentoPersonaPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private TipoDocumentoPersonaEntityMapper tipoDocumentoPersonaEntityMapper;

    @Override
    public List<TipoDocumentoPersona> buscarTiposDocumentoPersona(String cuo, Map<String, Object> filters) throws Exception {
        List<TipoDocumentoPersona> lista = new ArrayList<>();

        TypedQuery<MaeTipoDocumentoPersona> query = this.sf.getCurrentSession()
                .createNamedQuery(MaeTipoDocumentoPersona.Q_ALL, MaeTipoDocumentoPersona.class);

        query.getResultStream()
                .map(tipoDocumentoPersonaEntityMapper::toModel)
                .forEach(lista::add);

        return lista;
    }
}
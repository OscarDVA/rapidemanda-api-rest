package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

import pe.gob.pj.rapidemanda.domain.port.persistence.RecuperacionClavePersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovRecuperacionClaveEntity;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Component("recuperacionClavePersistencePort")
public class RecuperacionClavePersistenceAdapter implements RecuperacionClavePersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sfNegocio;

    @Override
    public void crearSolicitud(String cuo, String usuario, String tokenHash, Date expiraEn) throws Exception {
        // Buscar usuario activo por nombre de usuario en esquema negocio
        sfNegocio.getCurrentSession().enableFilter(MovUsuario.F_ACCESO)
                .setParameter(MovUsuario.P_ACTIVO, Estado.ACTIVO_NUMERICO.getNombre())
                .setParameter(MovUsuario.P_USUARIO, usuario);

        TypedQuery<MovUsuario> qUser = sfNegocio.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);
        MovUsuario movUsuario = qUser.getResultStream().findFirst().orElse(null);

        MovRecuperacionClaveEntity mov = new MovRecuperacionClaveEntity();
        mov.setUsuarioRecuperacion(movUsuario);
        mov.setTokenHash(tokenHash);
        mov.setFExpira(expiraEn);
        mov.setLUsado("0");
        mov.setActivo(Estado.ACTIVO_NUMERICO.getNombre());
        sfNegocio.getCurrentSession().persist(mov);
    }

    @Override
    public String obtenerUsuarioPorTokenActivo(String cuo, String tokenHash) throws Exception {
        String activo = Estado.ACTIVO_NUMERICO.getNombre();
        TypedQuery<MovRecuperacionClaveEntity> q = sfNegocio.getCurrentSession()
                .createQuery("select m from MovRecuperacionClaveEntity m "
                        + "where m.tokenHash = :token and m.lUsado = '0' and m.fExpira > CURRENT_TIMESTAMP and m.activo = :activo",
                        MovRecuperacionClaveEntity.class);
        q.setParameter("token", tokenHash);
        q.setParameter("activo", activo);
        MovRecuperacionClaveEntity mov = q.getResultStream().findFirst().orElse(null);
        return mov != null && mov.getUsuarioRecuperacion() != null ? mov.getUsuarioRecuperacion().getUsuario() : null;
    }

    @Override
    public void marcarUsado(String cuo, String tokenHash) throws Exception {
        TypedQuery<MovRecuperacionClaveEntity> q = sfNegocio.getCurrentSession()
                .createQuery("select m from MovRecuperacionClaveEntity m where m.tokenHash = :token",
                        MovRecuperacionClaveEntity.class);
        q.setParameter("token", tokenHash);
        MovRecuperacionClaveEntity mov = q.getResultStream().findFirst().orElse(null);
        if (mov != null) {
            mov.setLUsado("1");
            mov.setFUso(new Date());
            sfNegocio.getCurrentSession().merge(mov);
        }
    }
}
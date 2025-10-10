package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.*;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDashboardPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemanda;

@Slf4j
@Component("gestionDashboardPersistencePort")
public class GestionDashboardPersistenceAdapter implements GestionDashboardPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Override
    public DashboardResumen obtenerResumen(String cuo) throws Exception {
        DashboardResumen res = new DashboardResumen();
        try {
            Long total = (Long) sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md")
                    .getSingleResult();

            Long registrados = (Long) sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado")
                    .setParameter("estado", "P")
                    .getSingleResult();

            Long tipoM = (Long) sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoPresentacion.bTipoPresentacion = :tipo")
                    .setParameter("tipo", "M")
                    .getSingleResult();

            Long tipoF = (Long) sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoPresentacion.bTipoPresentacion = :tipo")
                    .setParameter("tipo", "F")
                    .getSingleResult();

            res.setTotalDemandas(total != null ? total : 0);
            res.setTotalRegistrados(registrados != null ? registrados : 0);
            res.setTotalPresentacionM(tipoM != null ? tipoM : 0);
            res.setTotalPresentacionF(tipoF != null ? tipoF : 0);
        } catch (Exception e) {
            log.error("{} Error obteniendo resumen dashboard: {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public List<DemandaResumen> listarDemandasRecientes(String cuo, int limite) throws Exception {
        List<DemandaResumen> lista = new ArrayList<>();
        try {
            TypedQuery<MovDemanda> q = sf.getCurrentSession().createQuery(
                    "SELECT md FROM MovDemanda md JOIN md.estadoDemanda ed JOIN md.tipoPresentacion tp ORDER BY md.fechaRecepcion DESC",
                    MovDemanda.class);
            q.setMaxResults(limite);

            q.getResultStream().forEach(md -> {
                DemandaResumen d = new DemandaResumen();
                d.setId(md.getId());
                try {
                    d.setFechaRecepcion(ProjectUtils.convertDateToString(md.getFechaRecepcion(), ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM_SS));
                } catch (Exception ex) {
                    d.setFechaRecepcion("");
                }
                try {
                    d.setEstado(md.getEstadoDemanda() != null ? md.getEstadoDemanda().getBEstadoDemanda() : null);
                } catch (Exception ex) {
                    d.setEstado(null);
                }
                try {
                    d.setTipoPresentacion(md.getTipoPresentacion() != null ? md.getTipoPresentacion().getBTipoPresentacion() : null);
                } catch (Exception ex) {
                    d.setTipoPresentacion(null);
                }
                lista.add(d);
            });
        } catch (Exception e) {
            log.error("{} Error listando demandas recientes: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public PetitorioConteos contarPetitorios(String cuo) throws Exception {
        PetitorioConteos res = new PetitorioConteos();
        try {
            // tipo
            List<Object[]> tipo = sf.getCurrentSession()
                    .createQuery("SELECT p.tipo, COUNT(p) FROM MovPetitorio p GROUP BY p.tipo")
                    .getResultList();
            res.setPorTipo(mapConteos(tipo));

            // pretensionPrincipal
            List<Object[]> pretension = sf.getCurrentSession()
                    .createQuery("SELECT p.pretensionPrincipal, COUNT(p) FROM MovPetitorio p GROUP BY p.pretensionPrincipal")
                    .getResultList();
            res.setPorPretensionPrincipal(mapConteos(pretension));

            // concepto
            List<Object[]> concepto = sf.getCurrentSession()
                    .createQuery("SELECT p.concepto, COUNT(p) FROM MovPetitorio p GROUP BY p.concepto")
                    .getResultList();
            res.setPorConcepto(mapConteos(concepto));

            // pretensionAccesoria
            List<Object[]> accesoria = sf.getCurrentSession()
                    .createQuery("SELECT p.pretensionAccesoria, COUNT(p) FROM MovPetitorio p GROUP BY p.pretensionAccesoria")
                    .getResultList();
            res.setPorPretensionAccesoria(mapConteos(accesoria));
        } catch (Exception e) {
            log.error("{} Error contando petitorios: {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public DemandanteConteos contarDemandantesSexoEdad(String cuo) throws Exception {
        DemandanteConteos res = new DemandanteConteos();
        try {
            // Por sexo
            List<Object[]> sexo = sf.getCurrentSession()
                    .createQuery("SELECT d.genero, COUNT(d) FROM MovDemandante d GROUP BY d.genero")
                    .getResultList();
            res.setPorSexo(mapConteos(sexo));

            // Por edad rango (cálculo en memoria)
            TypedQuery<Date> qFechas = sf.getCurrentSession()
                    .createQuery("SELECT d.fechaNacimiento FROM MovDemandante d", Date.class);
            List<Date> data = qFechas.getResultList();

            Map<String, Long> buckets = new HashMap<>();
            buckets.put("0-17", 0L);
            buckets.put("18-25", 0L);
            buckets.put("26-35", 0L);
            buckets.put("36-45", 0L);
            buckets.put("46-60", 0L);
            buckets.put("60+", 0L);

            Date now = new Date();
            for (Date fn : data) {
                if (fn == null) continue;
                int edad = calcularEdad(fn, now);
                String rango = obtenerRangoEdad(edad);
                buckets.put(rango, buckets.get(rango) + 1);
            }

            List<ConteoItem> rangos = new ArrayList<>();
            buckets.forEach((k, v) -> {
                ConteoItem c = new ConteoItem();
                c.setValor(k);
                c.setTotal(v);
                rangos.add(c);
            });
            res.setPorEdadRango(rangos);
        } catch (Exception e) {
            log.error("{} Error contando demandantes (sexo/edad): {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public List<ConteoParItem> contarDemandaPorTipoPresentacionYEstado(String cuo) throws Exception {
        List<ConteoParItem> lista = new ArrayList<>();
        try {
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery("SELECT md.tipoPresentacion.bTipoPresentacion, md.estadoDemanda.bEstadoDemanda, COUNT(md) FROM MovDemanda md GROUP BY md.tipoPresentacion.bTipoPresentacion, md.estadoDemanda.bEstadoDemanda")
                    .getResultList();
            rows.forEach(r -> {
                ConteoParItem item = new ConteoParItem();
                item.setClave1((String) r[0]);
                item.setClave2((String) r[1]);
                item.setTotal((Long) r[2]);
                lista.add(item);
            });
        } catch (Exception e) {
            log.error("{} Error contando demanda por tipoPresentacion y estado: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public List<ConteoItem> contarDemandaPorTipoPresentacion(String cuo) throws Exception {
        List<ConteoItem> lista = new ArrayList<>();
        try {
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery("SELECT md.tipoPresentacion.bTipoPresentacion, COUNT(md) FROM MovDemanda md GROUP BY md.tipoPresentacion.bTipoPresentacion")
                    .getResultList();
            lista = mapConteos(rows);
        } catch (Exception e) {
            log.error("{} Error contando demanda por tipoPresentacion: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    private List<ConteoItem> mapConteos(List<Object[]> rows) {
        List<ConteoItem> lista = new ArrayList<>();
        for (Object[] r : rows) {
            ConteoItem c = new ConteoItem();
            c.setValor(r[0] != null ? String.valueOf(r[0]) : null);
            c.setTotal(r[1] != null ? (Long) r[1] : 0);
            lista.add(c);
        }
        return lista;
    }

    private int calcularEdad(Date nacimiento, Date ahora) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(nacimiento);
        Calendar today = Calendar.getInstance();
        today.setTime(ahora);
        int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            years--;
        }
        return Math.max(years, 0);
    }

    private String obtenerRangoEdad(int edad) {
        if (edad <= 17) return "0-17";
        if (edad <= 25) return "18-25";
        if (edad <= 35) return "26-35";
        if (edad <= 45) return "36-45";
        if (edad <= 60) return "46-60";
        return "60+";
    }
}